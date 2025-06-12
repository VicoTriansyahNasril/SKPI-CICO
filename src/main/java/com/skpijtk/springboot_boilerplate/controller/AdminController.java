package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getAdminProfile(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        Long userId = Long.valueOf(userService.extractUserIdFromToken(token));
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "Admin profile retrieved successfully"));
    }

    @GetMapping("/total_mahasiswa")
    public ResponseEntity<ApiResponse<?>> getTotalMahasiswa() {
        long total = studentRepository.count();
        Map<String, Object> data = new HashMap<>();
        data.put("totalMahasiswa", total);
        return ResponseEntity.ok(ApiResponse.success(data, "Total mahasiswa retrieved successfully"));
    }

    @GetMapping("/resume_checkin")
    public ResponseEntity<ApiResponse<?>> getResumeCheckin() {
        try {
            LocalDate today = LocalDate.now();

            long totalMahasiswa = studentRepository.count();
            long totalCheckin = attendanceRepository.countByAttendanceDateAndCheckInTimeIsNotNull(today);
            long totalTelat = attendanceRepository.countByAttendanceDateAndIsLateTrue(today);
            long totalBelumCheckin = totalMahasiswa - totalCheckin;

            Map<String, Object> data = new HashMap<>();
            data.put("totalMahasiswa", totalMahasiswa);
            data.put("totalCheckin", totalCheckin);
            data.put("totalBelumCheckin", totalBelumCheckin);
            data.put("totalTelatCheckin", totalTelat);

            return ResponseEntity.ok(ApiResponse.success(data, "Resume checkin retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.success(null, "Gagal mengambil data resume checkin"));
        }
    }
}
