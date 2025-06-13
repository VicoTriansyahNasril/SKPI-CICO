package com.skpijtk.springboot_boilerplate.service.impl;

import com.skpijtk.springboot_boilerplate.dto.mahasiswa.MahasiswaResponse;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.service.StudentService;
import com.skpijtk.springboot_boilerplate.specification.AttendanceSpecification;
import com.skpijtk.springboot_boilerplate.specification.StudentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> getAllStudentsWithAttendance(String studentName, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nim").ascending());
        Page<Student> studentPage = studentRepository.findAll(StudentSpecification.byName(studentName), pageable);

        List<MahasiswaResponse> result = studentPage.stream().map(student -> {
            List<Attendance> attendances = attendanceRepository.findAll(
                    AttendanceSpecification.byStudentAndDateRange(student, startDate, endDate)
            );
            return MahasiswaResponse.from(student, attendances);
        }).collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", result);
        responseData.put("totalData", studentPage.getTotalElements());
        responseData.put("totalPage", studentPage.getTotalPages());
        responseData.put("currentPage", studentPage.getNumber());
        responseData.put("pageSize", studentPage.getSize());

        return ResponseEntity.ok(ApiResponse.success(responseData, "List all mahasiswa retrieved successfully"));
    }
}
