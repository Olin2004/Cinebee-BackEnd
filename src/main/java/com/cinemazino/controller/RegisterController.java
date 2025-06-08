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
        result.put("user", user);
        return ResponseEntity.ok(result);
    }
}
