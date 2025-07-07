package com.cinebee.service;

import com.cinebee.dto.request.LoginRequest;
import com.cinebee.dto.request.RegisterRequest;
import com.cinebee.dto.response.TokenResponse;
import com.cinebee.entity.User;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    User register(RegisterRequest request);
    TokenResponse login(LoginRequest request, HttpServletResponse response);
    TokenResponse refreshToken(String refreshToken, HttpServletResponse response);
    void logout(String accessToken);
    boolean verifyRecaptcha(String recaptchaToken);
    TokenResponse loginWithGoogleIdToken(String idToken, HttpServletResponse response);

    void forgotPassword(String email);

    String verifyOtp(com.cinebee.dto.request.VerifyOtpRequest request);

    void resetPassword(com.cinebee.dto.request.ResetPasswordRequest request);
}