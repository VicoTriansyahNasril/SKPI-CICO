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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MahasiswaServiceImpl implements MahasiswaService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

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

        String[] names = request.getStudentName().trim().split("\\s+");
        String firstName = names[0];
        String lastName = names[1];

        User user = student.getUser();
        user.setName(request.getStudentName());
        user.setEmail(request.getEmail());

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(request.getEmail());
        student.setNim(request.getNim());

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
    public ResponseEntity<Object> createMahasiswa(CreateMahasiswaRequest request) {
        return ResponseHandler.generateResponse(
                "Fitur tambah mahasiswa belum diimplementasikan",
                HttpStatus.NOT_IMPLEMENTED,
                null
        );
    }
}
