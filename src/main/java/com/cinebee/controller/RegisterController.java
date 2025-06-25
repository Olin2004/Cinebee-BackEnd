package com.cinebee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinebee.dto.request.RegisterRequest;
import com.cinebee.entity.User;
import com.cinebee.service.AuthService;

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
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("fullName", user.getFullName());
        result.put("phoneNumber", user.getPhoneNumber());
        result.put("dateOfBirth", user.getDateOfBirth());
        result.put("avatarUrl", user.getAvatarUrl());
        result.put("role", user.getRole());
        result.put("provider", user.getProvider());
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());
        result.put("userStatus", user.getUserStatus());
        return ResponseEntity.ok(result);
    }
}
