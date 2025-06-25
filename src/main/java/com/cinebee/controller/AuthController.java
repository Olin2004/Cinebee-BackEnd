package com.cinebee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.cinebee.dto.response.TokenResponse;
import com.cinebee.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        TokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token provided");
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        // Trả về message và token vừa logout
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {
            {
                put("message", "Logged out successfully");
                put("token", token);
            }
        });
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> loginWithGoogle(@RequestBody GoogleTokenRequest request) {
        // Xác thực Google ID token, lấy thông tin user, lưu DB, trả về JWT
        TokenResponse response = authService.loginWithGoogleIdToken(request.getIdToken());
        return ResponseEntity.ok(response);
    }


}

// DTO nhận Google ID token từ FE
class GoogleTokenRequest {
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
