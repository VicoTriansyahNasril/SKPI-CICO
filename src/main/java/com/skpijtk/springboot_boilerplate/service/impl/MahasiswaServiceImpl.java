package com.skpijtk.springboot_boilerplate.service.impl;

import com.skpijtk.springboot_boilerplate.dto.mahasiswa.CreateMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.dto.mahasiswa.EditMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.dto.mahasiswa.MahasiswaResponse;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import com.skpijtk.springboot_boilerplate.service.MahasiswaService;
import com.skpijtk.springboot_boilerplate.util.ResponseHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MahasiswaServiceImpl implements MahasiswaService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<Object> updateMahasiswa(Long studentId, EditMahasiswaRequest request) {
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            return ResponseHandler.generateResponse(
                    "Mahasiswa dengan ID tersebut tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    null
            );
        }

        String[] names = request.getStudentName().trim().split("\\s+", 2);
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";

        User user = student.getUser();
        user.setName(request.getStudentName());
        user.setEmail(request.getEmail());

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(request.getEmail());
        student.setNim(request.getNim());
        student.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        studentRepository.save(student);

        List<Attendance> attendances = attendanceRepository.findByStudent(student);

        return ResponseHandler.generateResponse(
                "Data mahasiswa berhasil diperbarui",
                HttpStatus.CREATED,
                MahasiswaResponse.from(student, attendances)
        );
    }

    @Override
    @Transactional
    public ResponseEntity<Object> createMahasiswa(CreateMahasiswaRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseHandler.generateResponse("Email sudah digunakan", HttpStatus.BAD_REQUEST, null);
        }

        User user = new User();
        user.setName(request.getStudentName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.MAHASISWA);
        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setNim(request.getNim());
        student.setEmail(request.getEmail());

        String[] names = request.getStudentName().split(" ", 2);
        student.setFirstName(names[0]);
        student.setLastName(names.length > 1 ? names[1] : "");

        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        studentRepository.save(student);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("studentId", student.getStudentId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());

        return ResponseHandler.generateResponse("Mahasiswa berhasil ditambahkan", HttpStatus.CREATED, response);
    }
}
