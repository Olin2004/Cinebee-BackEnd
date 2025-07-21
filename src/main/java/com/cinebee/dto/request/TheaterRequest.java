package com.cinebee.dto.request;

import com.cinebee.entity.Theater;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TheaterRequest {
    @NotBlank(message = "Theater name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String contactInfo;
    private String openingHours;

    @NotNull(message = "Status is required")
    private Theater.TheaterStatus status;
}
