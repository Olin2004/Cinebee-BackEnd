package com.cinemazino.service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cinemazino.config.JwtConfig;
import com.cinemazino.dto.request.LoginRequest;
import com.cinemazino.dto.request.RegisterRequest;
import com.cinemazino.dto.response.TokenResponse;
import com.cinemazino.entity.User;
import com.cinemazino.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
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
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already exists");
        }
        if (request.getDateOfBirth() != null
                && request.getDateOfBirth().plusYears(13).isAfter(java.time.LocalDate.now())) {
            throw new RuntimeException("Your age must be at least 13");
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
        user.setRole(User.Role.USER);
        user.setCreatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Gửi email chào mừng
        emailService.sendRegistrationSuccess(savedUser.getEmail(), savedUser.getFullName());

        return savedUser;
    }

    /**
     * Authenticate user by username, email, or phone number. Returns JWT tokens if
     * successful.
     * 
     * @param request Login data
     * @return TokenResponse containing JWT tokens and user role
     */
    public TokenResponse login(LoginRequest request) {
        if (!verifyTextCaptcha(request.getCaptchaKey(), request.getCaptcha())) {
            throw new RuntimeException("Captcha is incorrect or has expired");
        }

        Optional<User> userOpt = Optional.empty();
        String input = request.getUsername();
        if (input != null) {
            userOpt = userRepository.findByUsername(input);
            if (userOpt.isPresent() && userOpt.get().getRole() == User.Role.ADMIN) {
                if (!passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
                    throw new RuntimeException("Invalid username or password");
                }
            } else {
                userOpt = userRepository.findByEmail(input);
                if (userOpt.isEmpty()) {
                    userOpt = userRepository.findAll().stream()
                            .filter(u -> input.equals(u.getPhoneNumber()))
                            .findFirst();
                }
                if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
                    throw new RuntimeException("Invalid email/phone or password");
                }
            }
        }
        User user = userOpt.get();
        String accessToken = jwtConfig.generateToken(user);
        String refreshToken = jwtConfig.generateRefreshToken(user);
        redisTemplate.opsForValue().set(refreshToken, user.getUsername(), jwtConfig.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS);
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setRole(user.getRole().name());
        return response;
    }

    /**
     * Issue new access and refresh tokens if the provided refresh token is valid.
     * 
     * @param refreshToken The refresh token
     * @return TokenResponse with new tokens and user role
     */
    public TokenResponse refreshToken(String refreshToken) {
        String username = redisTemplate.opsForValue().get(refreshToken);
        if (username == null) {
            throw new RuntimeException("Invalid refresh token");
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        String newAccessToken = jwtConfig.generateToken(user);
        String newRefreshToken = jwtConfig.generateRefreshToken(user);
        redisTemplate.delete(refreshToken);
        redisTemplate.opsForValue().set(newRefreshToken, user.getUsername(), jwtConfig.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS);
        TokenResponse response = new TokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setRole(user.getRole().name());
        return response;
    }

    /**
     * Blacklist the provided access token until it expires.
     * 
     * @param accessToken The JWT access token to blacklist
     */
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

    /**
     * Login or register a user using Google information.
     * 
     * @param googleUser The JSON node containing Google user information
     * @return TokenResponse containing JWT tokens and user role
     */
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
            user.setRole(User.Role.USER);
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
        return response;
    }

    /**
     * Login or register a user using Google ID token.
     * 
     * @param idToken The Google ID token
     * @return TokenResponse containing JWT tokens and user role
     */
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
                throw new RuntimeException("Invalid Google ID token signature");

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
                user.setRole(User.Role.USER);
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
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Google login failed: " + e.getMessage());
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
        Map<String, Object> response = restTemplate.postForObject(url, params, Map.class);
        return response != null && Boolean.TRUE.equals(response.get("success"));
    }

    private boolean verifyTextCaptcha(String captchaKey, String captcha) {
        if (captchaKey == null || captcha == null)
            return false;
        String real = redisTemplate.opsForValue().get("captcha:" + captchaKey);
        return real != null && real.equalsIgnoreCase(captcha);
    }
}
