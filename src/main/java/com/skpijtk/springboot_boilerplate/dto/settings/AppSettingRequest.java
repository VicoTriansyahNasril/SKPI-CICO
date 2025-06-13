package com.skpijtk.springboot_boilerplate.dto.settings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AppSettingRequest {

    @NotBlank(message = "Check-in time is required")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "Format jam check-in harus HH:mm:ss")
    private String defaultCheckInTime;

    @NotBlank(message = "Check-out time is required")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "Format jam check-out harus HH:mm:ss")
    private String defaultCheckOutTime;

    @Min(value = 0, message = "Toleransi tidak boleh negatif")
    private int checkInLateToleranceMinutes;
}
