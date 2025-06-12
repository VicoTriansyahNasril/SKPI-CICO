package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getAdminProfile(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        Long userId = Long.valueOf(userService.extractUserIdFromToken(token));
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(ApiResponse.success(user, "Admin profile retrieved successfully"));
    }
}
