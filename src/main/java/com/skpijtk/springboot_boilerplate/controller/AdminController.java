package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.attendance.AttendanceResponse;
import com.skpijtk.springboot_boilerplate.dto.attendance.RiwayatAttendanceResponse;
import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.dto.settings.AppSettingRequest;
import com.skpijtk.springboot_boilerplate.dto.settings.AppSettingResponse;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.service.AppSettingService;
import com.skpijtk.springboot_boilerplate.service.StudentService;
import com.skpijtk.springboot_boilerplate.service.UserService;
import com.skpijtk.springboot_boilerplate.specification.AttendanceSpecification;
import com.skpijtk.springboot_boilerplate.util.ResponseHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;
    @Autowired
    private AppSettingService appSettingService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getAdminProfile(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        Long userId = Long.valueOf(userService.extractUserIdFromToken(token));
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "Admin profile retrieved successfully"));
    }

    @GetMapping("/total_mahasiswa")
    public ResponseEntity<ApiResponse<?>> getTotalMahasiswa() {
        long total = studentRepository.count();
        Map<String, Object> data = new HashMap<>();
        data.put("totalMahasiswa", total);
        return ResponseEntity.ok(ApiResponse.success(data, "Total mahasiswa retrieved successfully"));
    }

    @GetMapping("/resume_checkin")
    public ResponseEntity<ApiResponse<?>> getResumeCheckin() {
        LocalDate today = LocalDate.now();
        long totalMahasiswa = studentRepository.count();
        long totalCheckin = attendanceRepository.countByAttendanceDateAndCheckInTimeIsNotNull(today);
        long totalTelat = attendanceRepository.countByAttendanceDateAndIsLateTrue(today);
        long totalBelumCheckin = totalMahasiswa - totalCheckin;

        Map<String, Object> data = new HashMap<>();
        data.put("totalMahasiswa", totalMahasiswa);
        data.put("totalCheckin", totalCheckin);
        data.put("totalBelumCheckin", totalBelumCheckin);
        data.put("totalTelatCheckin", totalTelat);

        return ResponseEntity.ok(ApiResponse.success(data, "Resume checkin retrieved successfully"));
    }

    @GetMapping("/list_checkin_mahasiswa")
    public ResponseEntity<ApiResponse<?>> getAllCheckinList(
            @RequestParam(required = false) String student_name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enddate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String nimPrefix = null;
        String sortField = "attendanceDate";

        if (sortBy != null && sortBy.matches("^\\d+$")) {
            nimPrefix = sortBy;
        } else if (sortBy != null && !sortBy.isBlank()) {
            sortField = sortBy;
        }

        List<Attendance> filteredList = attendanceRepository.findAll(
                AttendanceSpecification.filterByCriteria(student_name, nimPrefix, startdate, enddate)
        );

        Comparator<Attendance> comparator;
        if (sortField.equalsIgnoreCase("nim")) {
            comparator = Comparator.comparing(a -> a.getStudent().getNim(), Comparator.nullsLast(String::compareToIgnoreCase));
        } else if (sortField.equalsIgnoreCase("attendanceDate")) {
            comparator = Comparator.comparing(Attendance::getAttendanceDate);
        } else {
            comparator = Comparator.comparing(Attendance::getAttendanceId);
        }

        if (sortDir.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        filteredList.sort(comparator);

        int start = page * size;
        int end = Math.min(start + size, filteredList.size());
        List<Attendance> pagedList = (start >= end) ? new ArrayList<>() : filteredList.subList(start, end);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Attendance a : pagedList) {
            Student s = a.getStudent();
            User u = s.getUser();

            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("studentId", s.getStudentId());
            studentMap.put("userId", u.getUserId());
            studentMap.put("studentName", u.getName());
            studentMap.put("nim", s.getNim());
            studentMap.put("email", s.getEmail());

            Map<String, Object> attendanceMap = new HashMap<>();
            attendanceMap.put("attendanceId", a.getAttendanceId());
            attendanceMap.put("checkinTime", a.getCheckInTime());
            attendanceMap.put("checkoutTime", a.getCheckOutTime());
            attendanceMap.put("attendanceDate", a.getAttendanceDate());
            attendanceMap.put("late", a.getIsLate());
            attendanceMap.put("notesCheckin", a.getCheckInNotes());
            attendanceMap.put("notesCheckout", a.getCheckOutNotes());
            attendanceMap.put("status", (a.getCheckInTime() != null ? "Checked-in" : "Not Checked-in"));

            studentMap.put("attendanceData", attendanceMap);
            dataList.add(studentMap);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", dataList);
        responseData.put("totalData", filteredList.size());
        responseData.put("totalPage", (int) Math.ceil((double) filteredList.size() / size));
        responseData.put("currentPage", page);
        responseData.put("pageSize", size);

        return ResponseEntity.ok(ApiResponse.success(responseData, "List check-in mahasiswa retrieved successfully"));
    }

    @GetMapping("/list_all_mahasiswa")
    public ResponseEntity<ApiResponse<?>> getAllMahasiswa(
            @RequestParam(required = false) String student_name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enddate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return studentService.getAllStudentsWithAttendance(student_name, startdate, enddate, page, size);
    }

    @DeleteMapping("/mahasiswa/{id_student}")
    public ResponseEntity<ApiResponse<?>> deleteMahasiswa(@PathVariable("id_student") Long studentId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", "Data mahasiswa tidak ditemukan", null)
            );
        }

        Student student = optionalStudent.get();
        User user = student.getUser();

        studentRepository.delete(student);

        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getStudentId());
        data.put("studentName", user.getName());
        data.put("nim", student.getNim());

        return ResponseEntity.ok(
            ApiResponse.success(data, "Data mahasiswa berhasil dihapus")
        );
    }

    @GetMapping("/mahasiswa/{id_student}")
    public ResponseEntity<ApiResponse<?>> getDetailMahasiswa(@PathVariable("id_student") Long studentId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Data mahasiswa tidak ditemukan"));
        }

        Student student = studentOpt.get();

        List<AttendanceResponse> attendanceList = attendanceRepository
                .findByStudentStudentId(studentId)
                .stream()
                .map(AttendanceResponse::from)
                .toList();

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("studentId", student.getStudentId());
        responseData.put("userId", student.getUser().getUserId());
        responseData.put("studentName", student.getFirstName() + " " + student.getLastName());
        responseData.put("nim", student.getNim());
        responseData.put("email", student.getEmail());
        responseData.put("attendanceData", attendanceList);

        return ResponseEntity.ok(ApiResponse.success(responseData, "Data mahasiswa berhasil diambil"));
    }

    @GetMapping("/list_attendance_mahasiswa")
    public ResponseEntity<?> getListAttendanceMahasiswa(
            @RequestParam("id_student") Long idStudent,
            @RequestParam(value = "startdate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "enddate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        if (!studentRepository.existsById(idStudent)) {
            throw new RuntimeException("Mahasiswa tidak ditemukan.");
        }

        List<Attendance> filteredAttendances = (startDate != null && endDate != null)
                ? attendanceRepository.findByStudentStudentIdAndAttendanceDateBetween(idStudent, startDate, endDate)
                : attendanceRepository.findByStudentStudentId(idStudent);

        int totalData = filteredAttendances.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalData);

        List<RiwayatAttendanceResponse> pagedList = (fromIndex < totalData)
                ? filteredAttendances.subList(fromIndex, toIndex).stream()
                    .map(RiwayatAttendanceResponse::fromEntity)
                    .toList()
                : Collections.emptyList();

        Map<String, Object> inner = new HashMap<>();
        inner.put("data", pagedList);
        inner.put("totalData", totalData);
        inner.put("totalPage", (int) Math.ceil((double) totalData / size));
        inner.put("currentPage", page);
        inner.put("pageSize", size);

        Map<String, Object> outer = new HashMap<>();
        outer.put("data", inner);
        outer.put("message", "Data attendance berhasil diambil.");
        outer.put("statusCode", 200);
        outer.put("status", "OK");

        return ResponseEntity.ok(outer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/system-settings")
    public ResponseEntity<Object> getSystemSettings() {
        try {
            Optional<AppSettingResponse> response = appSettingService.getAppSettings();
            if (response.isPresent()) {
                return ResponseHandler.generateResponse("Berhasil mendapatkan pengaturan sistem", HttpStatus.OK, response.get());
            } else {
                return ResponseHandler.generateResponse("Pengaturan sistem tidak ditemukan", HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Terjadi kesalahan saat memuat pengaturan sistem", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/system-settings")
    public ResponseEntity<Object> updateSystemSettings(@RequestBody AppSettingRequest request) {
        try {
            Optional<AppSettingResponse> updated = appSettingService.updateAppSettings(request);
            if (updated.isPresent()) {
                return ResponseHandler.generateResponse("Pengaturan berhasil diperbarui", HttpStatus.OK, updated.get());
            } else {
                return ResponseHandler.generateResponse("Pengaturan tidak ditemukan", HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Terjadi kesalahan saat memperbarui pengaturan", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
