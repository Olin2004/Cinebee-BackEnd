package com.cinemazino.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinemazino.config.JwtConfig;
import com.cinemazino.dto.request.LoginRequest;
import com.cinemazino.dto.request.RegisterRequest;
import com.cinemazino.dto.response.TokenResponse;
import com.cinemazino.entity.User;
import com.cinemazino.repository.UserRepository;

@Service
public class AuthService {
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
            throw new RuntimeException("Số điện thoại đã được sử dụng");
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
        return userRepository.save(user);
    }

    /**
     * Authenticate user by username, email, or phone number. Returns JWT tokens if
     * successful.
     * 
     * @param request Login data
     * @return TokenResponse containing JWT tokens and user role
     */
    public TokenResponse login(LoginRequest request) {
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
}
