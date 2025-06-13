package com.skpijtk.springboot_boilerplate.dto.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttendanceResponse {
    private Long attendanceId;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime checkinTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime checkoutTime;

    @JsonFormat(pattern = "dd MMMM yyyy", locale = "id")
    private LocalDate attendanceDate;

    private boolean late;
    private String notesCheckin;
    private String notesCheckout;
    private String status;

    public static AttendanceResponse from(Attendance att) {
        return AttendanceResponse.builder()
                .attendanceId(att.getAttendanceId())
                .checkinTime(att.getCheckInTime())
                .checkoutTime(att.getCheckOutTime())
                .attendanceDate(att.getAttendanceDate())
                .late(att.getIsLate())
                .notesCheckin(att.getCheckInNotes())
                .notesCheckout(att.getCheckOutNotes())
                .status(att.getCheckInTime() != null ? "Checked-in" : "Not Checked-in")
                .build();
    }
}
