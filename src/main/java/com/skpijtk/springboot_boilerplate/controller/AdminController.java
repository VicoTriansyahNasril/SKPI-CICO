package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.response.ApiResponse;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) String nim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enddate,
            @RequestParam(required = false, defaultValue = "attendanceDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<Attendance> result = attendanceRepository.findFilteredAttendances(student_name, nim, startdate, enddate);

        Comparator<Attendance> comparator;
        switch (sortBy) {
            case "nim" -> comparator = Comparator.comparing(a -> a.getStudent().getNim(), Comparator.nullsLast(String::compareToIgnoreCase));
            case "studentName" -> comparator = Comparator.comparing(
                    a -> (a.getStudent().getFirstName() + " " + a.getStudent().getLastName()), Comparator.nullsLast(String::compareToIgnoreCase));
            case "attendanceDate" -> comparator = Comparator.comparing(Attendance::getAttendanceDate, Comparator.nullsLast(LocalDate::compareTo));
            default -> comparator = Comparator.comparing(Attendance::getAttendanceDate, Comparator.nullsLast(LocalDate::compareTo));
        }

        if (sortDir.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        result.sort(comparator);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Attendance a : result) {
            Student s = a.getStudent();
            User u = s.getUser();

            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("studentId", s.getStudentId());
            studentMap.put("userId", u.getUserId());
            studentMap.put("studentName", s.getFirstName() + " " + s.getLastName());
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

        int totalData = dataList.size();
        int totalPage = (int) Math.ceil((double) totalData / size);
        int fromIndex = Math.min(page * size, totalData);
        int toIndex = Math.min(fromIndex + size, totalData);

        List<Map<String, Object>> paginatedData = dataList.subList(fromIndex, toIndex);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", paginatedData);
        responseData.put("totalData", totalData);
        responseData.put("totalPage", totalPage);
        responseData.put("currentPage", page);
        responseData.put("pageSize", size);

        return ResponseEntity.ok(ApiResponse.success(responseData, "List check-in mahasiswa retrieved successfully"));
    }
}
