package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.mahasiswa.CreateMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.dto.mahasiswa.EditMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.service.MahasiswaService;
import com.skpijtk.springboot_boilerplate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MahasiswaController {

    private final MahasiswaService mahasiswaService;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;

    @PostMapping("/admin/add-mahasiswa")
    public ResponseEntity<Object> createMahasiswa(@Valid @RequestBody CreateMahasiswaRequest request) {
        return mahasiswaService.createMahasiswa(request);
    }

    @PutMapping("/admin/edit-mahasiswa/{id}")
    public ResponseEntity<Object> updateMahasiswa(
            @PathVariable("id") Long id,
            @Valid @RequestBody EditMahasiswaRequest request) {
        return mahasiswaService.updateMahasiswa(id, request);
    }

    @GetMapping("/mahasiswa/profile")
    public ResponseEntity<Object> getProfileMahasiswa(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enddate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            String token = bearerToken.replace("Bearer ", "");
            Long userId = Long.valueOf(userService.extractUserIdFromToken(token));
            Optional<Student> studentOpt = studentRepository.findByUser_UserId(userId);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Mahasiswa tidak ditemukan"));
            }

            Student student = studentOpt.get();
            List<Attendance> attendanceList = (startdate != null && enddate != null)
                    ? attendanceRepository.findByStudentStudentIdAndAttendanceDateBetween(
                        student.getStudentId(), startdate, enddate)
                    : attendanceRepository.findByStudentStudentId(student.getStudentId());

            int totalData = attendanceList.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalData);

            List<Map<String, Object>> pagedAttendances = (fromIndex < totalData)
                    ? attendanceList.subList(fromIndex, toIndex).stream().map(a -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("attendanceId", a.getAttendanceId());
                        map.put("checkinTime", a.getCheckInTime());
                        map.put("checkoutTime", a.getCheckOutTime());
                        map.put("attendanceDate", a.getAttendanceDate());
                        map.put("late", a.getIsLate());
                        map.put("notesCheckin", a.getCheckInNotes());
                        map.put("notesCheckout", a.getCheckOutNotes());
                        map.put("status", a.getCheckInTime() != null ? "Checked-in" : "Not Checked-in");
                        return map;
                    }).collect(Collectors.toList())
                    : Collections.emptyList();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", student.getStudentId());
            data.put("studentName", student.getFirstName() + " " + student.getLastName());
            data.put("nim", student.getNim());
            data.put("attendanceData", pagedAttendances);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", data);
            response.put("totalData", totalData);
            response.put("totalPage", (int) Math.ceil((double) totalData / size));
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("message", "Profil mahasiswa berhasil diambil");
            response.put("statusCode", 200);
            response.put("status", "OK");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("Terjadi kesalahan saat mengambil profil mahasiswa"));
        }
    }

    @PostMapping("/mahasiswa/checkin")
    public ResponseEntity<Object> checkinMahasiswa(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, String> requestBody
    ) {
        try {
            String token = bearerToken.replace("Bearer ", "");
            Long userId = Long.valueOf(userService.extractUserIdFromToken(token));
            Optional<Student> studentOpt = studentRepository.findByUser_UserId(userId);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.badRequest("Mahasiswa tidak ditemukan"));
            }

            Student student = studentOpt.get();
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();

            boolean alreadyCheckedIn = attendanceRepository
                    .findByStudentStudentIdAndAttendanceDate(student.getStudentId(), today)
                    .isPresent();

            if (alreadyCheckedIn) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.badRequest("Anda sudah melakukan check-in hari ini"));
            }

            String notes = requestBody.get("notesCheckin");
            if (notes == null || notes.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.badRequest("Catatan check-in wajib diisi"));
            }

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setAttendanceDate(today);
            attendance.setCheckInTime(now);
            attendance.setCheckInNotes(notes);
            attendance.setCheckOutTime(null);
            attendance.setCheckOutNotes(null);
            attendance.setIsLate(now.toLocalTime().isAfter(LocalTime.of(8, 0)));
            attendance.setCreatedAt(now);

            attendance.setUpdatedAt(now);

            attendanceRepository.save(attendance);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", student.getStudentId());
            data.put("studentName", student.getFirstName() + " " + student.getLastName());
            data.put("nim", student.getNim());
            data.put("attendanceId", attendance.getAttendanceId());
            data.put("checkinTime", attendance.getCheckInTime());
            data.put("checkOutTime", attendance.getCheckOutTime());
            data.put("attendanceDate", attendance.getAttendanceDate());
            data.put("notesCheckin", attendance.getCheckInNotes());
            data.put("notesCheckout", attendance.getCheckOutNotes());
            data.put("statusCheckin", "Checked-in");

            return ResponseEntity.ok(
                    ApiResponse.success(data, "Check-in berhasil")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("Terjadi kesalahan saat melakukan check-in"));
        }
    }

    @PostMapping("/mahasiswa/checkout")
    public ResponseEntity<Object> checkoutMahasiswa(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, String> requestBody
    ) {
        try {
            String token = bearerToken.replace("Bearer ", "");
            Long userId = Long.valueOf(userService.extractUserIdFromToken(token));
            Optional<Student> studentOpt = studentRepository.findByUser_UserId(userId);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Mahasiswa tidak ditemukan untuk user ini."));
            }

            Student student = studentOpt.get();
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();

            Optional<Attendance> attendanceOpt = attendanceRepository
                    .findByStudentStudentIdAndAttendanceDate(student.getStudentId(), today);

            if (attendanceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.badRequest("Anda belum melakukan check-in hari ini."));
            }

            Attendance attendance = attendanceOpt.get();

            if (attendance.getCheckOutTime() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.badRequest("Anda sudah melakukan check-out hari ini."));
            }

            String notes = requestBody.get("notesCheckout");
            if (notes == null || notes.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.badRequest("Catatan check-out tidak boleh kosong."));
            }

            attendance.setCheckOutTime(now);
            attendance.setCheckOutNotes(notes);
            attendance.setUpdatedAt(now);
            attendanceRepository.save(attendance);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", student.getStudentId());
            data.put("studentName", student.getFirstName() + " " + student.getLastName());
            data.put("nim", student.getNim());
            data.put("attendanceId", attendance.getAttendanceId());
            data.put("checkinTime", attendance.getCheckInTime());
            data.put("checkOutTime", attendance.getCheckOutTime());
            data.put("attendanceDate", attendance.getAttendanceDate());
            data.put("notesCheckin", attendance.getCheckInNotes());
            data.put("notesCheckout", attendance.getCheckOutNotes());
            data.put("statusCheckin", "Checked-in");

            return ResponseEntity.ok(
                    ApiResponse.success(data, "Berhasil melakukan check-out.")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("Terjadi kesalahan saat proses check-out."));
        }
    }
}
