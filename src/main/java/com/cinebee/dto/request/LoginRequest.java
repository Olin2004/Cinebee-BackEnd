package com.cinebee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username or email is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    private String recaptchaToken;
    private String captchaKey;
    private String captcha;


}