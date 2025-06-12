package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.auth.LoginRequest;
import com.skpijtk.springboot_boilerplate.dto.auth.LoginResponse;
import com.skpijtk.springboot_boilerplate.dto.auth.RegisterRequest;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        ApiResponse<?> response = authService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginAdmin(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.loginAdmin(request);
        return ResponseEntity.ok(
            ApiResponse.success(response, "Login successful")
        );
    }
}
