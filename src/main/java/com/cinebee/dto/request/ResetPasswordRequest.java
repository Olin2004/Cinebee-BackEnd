package com.cinebee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank

    private String temporaryToken;

    @NotBlank
    private String newPassword;
}
