package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.settings.AppSettingRequest;
import com.skpijtk.springboot_boilerplate.dto.settings.AppSettingResponse;

import java.util.Optional;

public interface AppSettingService {
    Optional<AppSettingResponse> getAppSettings();
    Optional<AppSettingResponse> updateAppSettings(AppSettingRequest request);
}
