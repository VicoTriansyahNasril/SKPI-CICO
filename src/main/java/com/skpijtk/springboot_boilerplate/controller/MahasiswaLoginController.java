package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.auth.LoginRequest;
import com.skpijtk.springboot_boilerplate.dto.auth.LoginResponse;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mahasiswa")
@RequiredArgsConstructor
public class MahasiswaLoginController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginMahasiswa(@Valid @RequestBody LoginRequest request) {
        try {
            ApiResponse<LoginResponse> response = authService.loginMahasiswa(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(
                ApiResponse.error(null, ex.getMessage(), 401, "UNAUTHORIZED")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                ApiResponse.error(null, "Terjadi kesalahan saat login", 500, "INTERNAL_SERVER_ERROR")
            );
        }
    }
} 
