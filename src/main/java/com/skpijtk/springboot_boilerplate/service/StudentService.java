package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface StudentService {
    ResponseEntity<ApiResponse<?>> getAllStudentsWithAttendance(String studentName, LocalDate startDate, LocalDate endDate, int page, int size);
}
