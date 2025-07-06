package com.cinebee.controller;

import com.cinebee.dto.request.GoogleTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import com.cinebee.dto.response.TokenResponse;
import com.cinebee.service.AuthService;
import com.cinebee.util.TokenCookieUtil;



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


}

