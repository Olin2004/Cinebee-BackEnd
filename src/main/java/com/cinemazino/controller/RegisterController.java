package com.cinemazino.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinemazino.dto.request.RegisterRequest;
import com.cinemazino.entity.User;
import com.cinemazino.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {
    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("status", 200);
        result.put("message", "Register successful");
        // Chuẩn hóa dateOfBirth trả về dạng dd/MM/yyyy
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.util.Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("password", user.getPassword());
        userMap.put("role", user.getRole());
        userMap.put("email", user.getEmail());
        userMap.put("fullName", user.getFullName());
        userMap.put("phoneNumber", user.getPhoneNumber());
        userMap.put("dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth().format(dateFormatter) : null);
        userMap.put("avatarUrl", user.getAvatarUrl());
        userMap.put("provider", user.getProvider());
        userMap.put("oauthId", user.getOauthId());
        userMap.put("createdAt", user.getCreatedAt());
        userMap.put("updatedAt", user.getUpdatedAt());
        result.put("user", userMap);
        return ResponseEntity.ok(result);
    }
}
