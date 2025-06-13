package com.skpijtk.springboot_boilerplate.dto.settings;

public class AppSettingResponse {
    private String defaultCheckInTime;
    private String defaultCheckOutTime;
    private Integer checkInLateToleranceMinutes;

    public AppSettingResponse() {}

    public AppSettingResponse(String defaultCheckInTime, String defaultCheckOutTime, Integer checkInLateToleranceMinutes) {
        this.defaultCheckInTime = defaultCheckInTime;
        this.defaultCheckOutTime = defaultCheckOutTime;
        this.checkInLateToleranceMinutes = checkInLateToleranceMinutes;
    }

    public String getDefaultCheckInTime() {
        return defaultCheckInTime;
    }

    public void setDefaultCheckInTime(String defaultCheckInTime) {
        this.defaultCheckInTime = defaultCheckInTime;
    }

    public String getDefaultCheckOutTime() {
        return defaultCheckOutTime;
    }

    public void setDefaultCheckOutTime(String defaultCheckOutTime) {
        this.defaultCheckOutTime = defaultCheckOutTime;
    }

    public Integer getCheckInLateToleranceMinutes() {
        return checkInLateToleranceMinutes;
    }

    public void setCheckInLateToleranceMinutes(Integer checkInLateToleranceMinutes) {
        this.checkInLateToleranceMinutes = checkInLateToleranceMinutes;
    }
}
