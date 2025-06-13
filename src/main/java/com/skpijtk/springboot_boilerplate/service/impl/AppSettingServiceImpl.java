package com.skpijtk.springboot_boilerplate.service.impl;

import com.skpijtk.springboot_boilerplate.dto.settings.AppSettingRequest;
import com.skpijtk.springboot_boilerplate.dto.settings.AppSettingResponse;
import com.skpijtk.springboot_boilerplate.model.AppSetting;
import com.skpijtk.springboot_boilerplate.repository.AppSettingRepository;
import com.skpijtk.springboot_boilerplate.service.AppSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppSettingServiceImpl implements AppSettingService {

    @Autowired
    private AppSettingRepository appSettingRepository;

    @Override
    public Optional<AppSettingResponse> getAppSettings() {
        try {
            String checkIn = appSettingRepository.findById("default_check_in_time")
                    .map(AppSetting::getSettingValue)
                    .orElse(null);

            String checkOut = appSettingRepository.findById("default_check_out_time")
                    .map(AppSetting::getSettingValue)
                    .orElse(null);

            int tolerance = appSettingRepository.findById("check_in_late_tolerance_minutes")
                    .map(s -> Integer.parseInt(s.getSettingValue()))
                    .orElse(0);

            AppSettingResponse response = new AppSettingResponse(checkIn, checkOut, tolerance);
            return Optional.of(response);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AppSettingResponse> updateAppSettings(AppSettingRequest request) {
        try {
            AppSetting checkInSetting = appSettingRepository.findById("default_check_in_time")
                    .orElse(new AppSetting("default_check_in_time", "", "Waktu check-in default"));

            AppSetting checkOutSetting = appSettingRepository.findById("default_check_out_time")
                    .orElse(new AppSetting("default_check_out_time", "", "Waktu check-out default"));

            AppSetting toleranceSetting = appSettingRepository.findById("check_in_late_tolerance_minutes")
                    .orElse(new AppSetting("check_in_late_tolerance_minutes", "", "Toleransi keterlambatan dalam menit"));

            checkInSetting.setSettingValue(request.getDefaultCheckInTime());
            checkOutSetting.setSettingValue(request.getDefaultCheckOutTime());
            toleranceSetting.setSettingValue(String.valueOf(request.getCheckInLateToleranceMinutes()));

            appSettingRepository.save(checkInSetting);
            appSettingRepository.save(checkOutSetting);
            appSettingRepository.save(toleranceSetting);

            AppSettingResponse response = new AppSettingResponse(
                    request.getDefaultCheckInTime(),
                    request.getDefaultCheckOutTime(),
                    request.getCheckInLateToleranceMinutes()
            );

            return Optional.of(response);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
