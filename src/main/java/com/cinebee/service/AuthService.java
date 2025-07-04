package com.cinebee.service;

import java.util.Map;
import java.util.Optional;
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

import com.cinebee.util.UsernameGenerator;

@Service
public class AuthService {
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UsernameGenerator usernameGenerator;
    @Autowired
    private GoogleOAuth2Service googleOAuth2Service;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TokenService tokenService;

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
        String username = usernameGenerator.generateBaseUsername(request.getFullName());
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
        return tokenService.createTokenResponse(user, 0);
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
            userOpt = userRepository.findByPhoneNumber(input);
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
        return tokenService.refreshToken(refreshToken);
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
            tokenService.blacklistToken(accessToken, ttl);
        }
    }



    public boolean verifyRecaptcha(String recaptchaToken) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
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

    public TokenResponse loginWithGoogleIdToken(String idToken) {
        return googleOAuth2Service.loginWithGoogleIdToken(idToken);
    }
}