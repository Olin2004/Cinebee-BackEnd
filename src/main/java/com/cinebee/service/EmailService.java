package com.cinebee.service;

public interface EmailService {
    void sendRegistrationSuccess(String to, String name);
    void sendPasswordResetOtp(String to, String otp);
}
