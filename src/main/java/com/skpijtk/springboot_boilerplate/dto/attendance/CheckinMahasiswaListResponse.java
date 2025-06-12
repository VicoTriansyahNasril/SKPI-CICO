package com.skpijtk.springboot_boilerplate.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CheckinMahasiswaListResponse {
    private Long studentId;
    private Long userId;
    private String studentName;
    private String nim;
    private String email;
    private Long attendanceId;
    private LocalDate attendanceDate;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private Boolean late;
    private String notesCheckin;
    private String notesCheckout;
    private String status;
}
