package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.auth.RegisterRequest;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Register Admin", description = "Membuat akun admin baru")
    public ResponseEntity<ApiResponse<?>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        ApiResponse<?> response = authService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }
}
