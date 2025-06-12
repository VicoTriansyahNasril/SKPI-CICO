package com.skpijtk.springboot_boilerplate.service.impl;

import com.skpijtk.springboot_boilerplate.dto.auth.*;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.exception.EmailAlreadyExistsException;
import com.skpijtk.springboot_boilerplate.exception.ValidationException;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import com.skpijtk.springboot_boilerplate.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ApiResponse<?> registerAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Username or Email has been used");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.ADMIN);

        userRepository.save(user);

        return new ApiResponse<>(200, "OK", "Signup successful", new RegisterResponse(user.getEmail()));
    }

    @Override
    public LoginResponse loginAdmin(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("T-ERR-EMAIL-NOT-REGISTERED"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()) ||
            user.getRole() != User.Role.ADMIN) {
            throw new ValidationException("T-ERR-INVALID-CREDENTIALS");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getRole());

        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getRole().name(),
                token);
    }

    @Override
    public AdminProfileResponse getAdminProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        return AdminProfileResponse.builder()
                .name(user.getName())
                .role(user.getRole().name())
                .time(AdminProfileResponse.getFormattedTime())
                .build();
    }
}
