package com.cinebee.service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cinebee.config.JwtConfig;
import com.cinebee.common.Role;
import com.cinebee.dto.request.LoginRequest;
import com.cinebee.dto.request.RegisterRequest;
import com.cinebee.dto.response.TokenResponse;
import com.cinebee.entity.User;
import com.cinebee.exception.ApiException;
import com.cinebee.exception.ErrorCode;
import com.cinebee.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class AuthService {
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private EmailService emailService;

    private static final int MAX_REFRESH_COUNT = 5;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Register a new user with unique email and phone number. Username is
     * auto-generated.
     * Throws an error if email or phone number already exists, or if user is under
     * 13 years old.
     *
     * @param request Registration data
     * @return The created User entity
     */
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new ApiException(ErrorCode.USER_EXISTED);
        }
        if (request.getDateOfBirth() != null
                && request.getDateOfBirth().plusYears(13).isAfter(java.time.LocalDate.now())) {
            throw new ApiException(ErrorCode.INVALID_DOB);
        }
        String baseUsername = removeVietnameseTones(request.getFullName().trim().split("\\s+")[0].toLowerCase());
        String username;
        do {
            int randomNum = (int) (Math.random() * 1_000_000_000);
            username = baseUsername + randomNum;
        } while (userRepository.findByUsername(username).isPresent());
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(Role.USER);
        user.setCreatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);
        emailService.sendRegistrationSuccess(savedUser.getEmail(), savedUser.getFullName());
        return savedUser;
    }

    /**

     * @param request Login data
     * @return TokenResponse containing JWT tokens and user role
     */
    public TokenResponse login(LoginRequest request) {
        validateLoginRequest(request);
        User user = getUserForLogin(request);
        String accessToken = jwtConfig.generateToken(user);
        String refreshToken = jwtConfig.generateRefreshToken(user);
        saveRefreshToken(refreshToken, user.getUsername(), jwtConfig.getRefreshExpirationMs());
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setRole(user.getRole().name());
        response.setUserStatus(user.getUserStatus() != null ? user.getUserStatus().name() : null);
        return response;
    }

    private void validateLoginRequest(LoginRequest request) {
        String input = request.getUsername();
        String password = request.getPassword();
        if (input == null || input.trim().isEmpty() || input.length() < 3) {
            throw new ApiException(ErrorCode.USERNAME_OR_PHONE_INVALID);
        }
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            throw new ApiException(ErrorCode.PASSWORD_INVALID);
        }
        boolean isEmail = input.contains("@");
        boolean isPhone = input.matches("^0[0-9]{9}$"); // Vietnamese phone number: 10 digits, starts with 0
        if (isEmail && !Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(input).matches()) {
            throw new ApiException(ErrorCode.EMAIL_INVALID);
        }
        if (isPhone && !Pattern.compile("^0[0-9]{9}$").matcher(input).matches()) {
            throw new ApiException(ErrorCode.PHONE_INVALID_FORMAT);
        }
        // If captchaKey and captcha are present, verify captcha
        if (request.getCaptchaKey() != null && request.getCaptcha() != null) {
            if (!verifyTextCaptcha(request.getCaptchaKey(), request.getCaptcha())) {
                throw new ApiException(ErrorCode.CAPTCHA_INVALID);
            }
        }
    }

    private User getUserForLogin(LoginRequest request) {
        String input = request.getUsername();
        Optional<User> userOpt = userRepository.findByUsername(input);
        if (userOpt.isPresent() && userOpt.get().getRole() == Role.ADMIN) {
            if (!passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
                throw new ApiException(ErrorCode.UNAUTHORIZED);
            }
            return userOpt.get();
        }
        userOpt = userRepository.findByEmail(input);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findAll().stream()
                    .filter(u -> input.equals(u.getPhoneNumber()))
                    .findFirst();
        }
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        return userOpt.get();
    }

    /**

     * @return TokenResponse with new tokens and user role
     */
    public TokenResponse refreshToken(String refreshToken) {
        String redisKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            throw new ApiException(ErrorCode.TOKEN_INVALID);
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(value, Map.class);
            String username = (String) data.get("username");
            int refreshCount = (int) (data.get("refresh_count") instanceof Integer ? data.get("refresh_count") : ((Number)data.get("refresh_count")).intValue());
            if (refreshCount >= MAX_REFRESH_COUNT) {
                redisTemplate.delete(redisKey);
                throw new ApiException(ErrorCode.REFRESH_TOKEN_LIMIT_EXCEEDED);
            }
            User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTED));
            String newAccessToken = jwtConfig.generateToken(user);
            String newRefreshToken = jwtConfig.generateRefreshToken(user);
            // Xóa token cũ, lưu token mới với refresh_count + 1
            redisTemplate.delete(redisKey);
            saveRefreshTokenWithCount(newRefreshToken, user.getUsername(), refreshCount + 1, jwtConfig.getRefreshExpirationMs());
            TokenResponse response = new TokenResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setRole(user.getRole().name());
            return response;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR);
        }
    }


    public void logout(String accessToken) {
        long now = System.currentTimeMillis();
        long exp = 0;
        try {
            exp = jwtConfig.getClaims(accessToken).getExpiration().getTime();
        } catch (Exception e) {
            return;
        }
        long ttl = exp - now;
        if (ttl > 0) {
            tokenBlacklistService.blacklist(accessToken, ttl);
        }
    }


    public TokenResponse loginWithGoogle(JsonNode googleUser) {
        String email = googleUser.get("email").asText();
        String sub = googleUser.get("sub").asText(); // Google user ID
        String name = googleUser.has("name") ? googleUser.get("name").asText() : null;
        String picture = googleUser.has("picture") ? googleUser.get("picture").asText() : null;
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            // Nếu user đã tồn tại nhưng chưa có oauthId hoặc provider, cập nhật
            if (user.getOauthId() == null)
                user.setOauthId(sub);
            if (user.getProvider() != User.Provider.GOOGLE)
                user.setProvider(User.Provider.GOOGLE);
            if (name != null && (user.getFullName() == null || user.getFullName().isEmpty()))
                user.setFullName(name);
            if (picture != null && (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()))
                user.setAvatarUrl(picture);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            user = new User();
            user.setUsername(generateGoogleUsername(email));
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // random password
            user.setEmail(email);
            user.setFullName(name);
            user.setAvatarUrl(picture);
            user.setProvider(User.Provider.GOOGLE);
            user.setOauthId(sub);
            user.setRole(Role.USER);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
        String accessToken = jwtConfig.generateToken(user);
        String refreshToken = jwtConfig.generateRefreshToken(user);
        redisTemplate.opsForValue().set(refreshToken, user.getUsername(), jwtConfig.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS);
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setRole(user.getRole().name());
        response.setUserStatus(user.getUserStatus() != null ? user.getUserStatus().name() : null);
        return response;
    }

    public TokenResponse loginWithGoogleIdToken(String idToken) {
        try {
            // 1. Parse token
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String email = claims.getStringClaim("email");
            String sub = claims.getSubject();
            String name = claims.getStringClaim("name");
            String picture = claims.getStringClaim("picture");

            // 2. Validate signature (optional, Google SDK đã validate ở FE, nhưng có thể
            // check thêm)
            // Lấy public key từ Google (caching production)
            URL jwksUrl = new URL("https://www.googleapis.com/oauth2/v3/certs");
            JWKSet publicKeys = JWKSet.load(jwksUrl);
            List<JWK> keys = publicKeys.getKeys();
            boolean valid = false;
            for (JWK key : keys) {
                if (key instanceof RSAKey) {
                    RSAPublicKey rsaPublicKey = ((RSAKey) key).toRSAPublicKey();
                    JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);
                    if (signedJWT.verify(verifier)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid)
                throw new ApiException(ErrorCode.UNAUTHORIZED);

            // 3. Lưu user như loginWithGoogle(JsonNode)
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
                if (user.getOauthId() == null)
                    user.setOauthId(sub);
                if (user.getProvider() != User.Provider.GOOGLE)
                    user.setProvider(User.Provider.GOOGLE);
                if (name != null && (user.getFullName() == null || user.getFullName().isEmpty()))
                    user.setFullName(name);
                if (picture != null && (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()))
                    user.setAvatarUrl(picture);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            } else {
                user = new User();
                user.setUsername(generateGoogleUsername(email));
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setEmail(email);
                user.setFullName(name);
                user.setAvatarUrl(picture);
                user.setProvider(User.Provider.GOOGLE);
                user.setOauthId(sub);
                user.setRole(Role.USER);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
            String accessToken = jwtConfig.generateToken(user);
            String refreshToken = jwtConfig.generateRefreshToken(user);
            saveRefreshToken(refreshToken, user.getUsername(), jwtConfig.getRefreshExpirationMs());
            TokenResponse response = new TokenResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setRole(user.getRole().name());
            response.setUserStatus(user.getUserStatus() != null ? user.getUserStatus().name() : null);
            return response;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String generateGoogleUsername(String email) {
        String base = email.split("@")[0];
        String username = base;
        int i = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = base + i;
            i++;
        }
        return username;
    }

    /**
     * Remove Vietnamese diacritics from a string for username generation.
     *
     * @param str Input string
     * @return String without Vietnamese tones
     */
    private String removeVietnameseTones(String str) {
        str = str.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        str = str.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        str = str.replaceAll("[ìíịỉĩ]", "i");
        str = str.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        str = str.replaceAll("[ùúụủũưừứựửữ]", "u");
        str = str.replaceAll("[ỳýỵỷỹ]", "y");
        str = str.replaceAll("đ", "d");
        str = str.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        str = str.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        str = str.replaceAll("[ÌÍỊỈĨ]", "I");
        str = str.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        str = str.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        str = str.replaceAll("[ỲÝỴỶỸ]", "Y");
        str = str.replaceAll("Đ", "D");
        str = str.replaceAll("[^a-zA-Z0-9]", "");
        return str;
    }

    public boolean verifyRecaptcha(String recaptchaToken) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", recaptchaToken);
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(url, params, Map.class);
        return response != null && Boolean.TRUE.equals(response.get("success"));
    }

    private boolean verifyTextCaptcha(String captchaKey, String captcha) {
        if (captchaKey == null || captcha == null)
            return false;
        String real = redisTemplate.opsForValue().get("captcha:" + captchaKey);
        return real != null && real.equalsIgnoreCase(captcha);
    }

    // Khi sinh refresh token, lưu refresh_count = 0
    private void saveRefreshToken(String refreshToken, String username, long ttlMillis) {
        try {
            String value = objectMapper.writeValueAsString(Map.of(
                    "username", username,
                    "refresh_count", 0
            ));
            redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, value, ttlMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // Khi update refresh token, tăng refresh_count
    private void saveRefreshTokenWithCount(String refreshToken, String username, int refreshCount, long ttlMillis) {
        try {
            String value = objectMapper.writeValueAsString(Map.of(
                    "username", username,
                    "refresh_count", refreshCount
            ));
            redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, value, ttlMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR);
        }
    }
}