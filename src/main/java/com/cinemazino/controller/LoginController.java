package com.cinemazino.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cinemazino.dto.request.LoginRequest;
import com.cinemazino.dto.response.TokenResponse;
import com.cinemazino.service.AuthService;
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
        result.put("status", 200);
        result.put("message", "Login successful");
        result.put("accessToken", response.getAccessToken());
        result.put("refreshToken", response.getRefreshToken());
        result.put("role", response.getRole());
        return ResponseEntity.ok(result);
    }
}
