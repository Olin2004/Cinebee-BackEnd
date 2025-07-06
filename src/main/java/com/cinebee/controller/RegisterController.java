package com.cinebee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinebee.dto.request.RegisterRequest;
import com.cinebee.dto.response.UserResponse;
import com.cinebee.entity.User;
import com.cinebee.mapper.UserMapper;
import com.cinebee.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {
    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }
}
