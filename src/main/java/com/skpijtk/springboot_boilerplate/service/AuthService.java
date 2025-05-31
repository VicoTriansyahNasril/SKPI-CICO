package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.auth.RegisterRequest;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;

public interface AuthService {
    ApiResponse<?> registerAdmin(RegisterRequest request);
}
