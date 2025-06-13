package com.skpijtk.springboot_boilerplate.dto.attendance;

import com.skpijtk.springboot_boilerplate.model.Attendance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiwayatAttendanceResponse {

    private Long studentId;
    private Long userId;
    private String studentName;
    private String nim;
    private String email;
    private AttendanceData attendanceData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttendanceData {
        private Long attendanceId;
        private LocalDateTime checkinTime;
        private LocalDateTime checkoutTime;
        private LocalDate attendanceDate;
        private Boolean late;
        private String notesCheckin;
        private String notesCheckout;
        private String status;
    }

    public static RiwayatAttendanceResponse fromEntity(Attendance attendance) {
    String calculatedStatus;
    if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
        calculatedStatus = "Hadir";
    } else if (attendance.getCheckInTime() != null) {
        calculatedStatus = "Belum Checkout";
    } else {
        calculatedStatus = "Belum Hadir";
    }

     return new RiwayatAttendanceResponse(
            attendance.getStudent().getStudentId(),
            attendance.getStudent().getUser().getUserId(),
            attendance.getStudent().getUser().getName(),
            attendance.getStudent().getNim(),
            attendance.getStudent().getUser().getEmail(),
            new AttendanceData(
                    attendance.getAttendanceId(),
                    attendance.getCheckInTime(),
                    attendance.getCheckOutTime(),
                    attendance.getAttendanceDate(),
                    attendance.getIsLate(),
                    attendance.getCheckInNotes(),
                    attendance.getCheckOutNotes(),
                    calculatedStatus
                )
        );
    }

}
