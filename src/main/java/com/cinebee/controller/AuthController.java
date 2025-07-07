package com.cinebee.controller;

import com.cinebee.dto.request.GoogleTokenRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import com.cinebee.dto.response.TokenResponse;
import com.cinebee.service.AuthService;




@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "accessToken") String accessToken) {
        authService.logout(accessToken);
        // Xóa cookie accessToken và refreshToken phía client
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Logged out successfully");
        }});
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> loginWithGoogle(@RequestBody GoogleTokenRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.loginWithGoogleIdToken(request.getIdToken(), response);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody com.cinebee.dto.request.ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Password reset link sent to your email");
        }});
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody com.cinebee.dto.request.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Password reset successfully");
        }});
    }
}

