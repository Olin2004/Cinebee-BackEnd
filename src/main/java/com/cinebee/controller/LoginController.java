package com.cinebee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cinebee.dto.request.LoginRequest;
import com.cinebee.dto.response.TokenResponse;
import com.cinebee.service.AuthService;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth/login")
public class LoginController {
    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("accessToken", response.getAccessToken());
        result.put("refreshToken", response.getRefreshToken());
        result.put("userStatus", response.getUserStatus());
        result.put("role", response.getRole());
        return ResponseEntity.ok(result);
    }
}
