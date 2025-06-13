package com.skpijtk.springboot_boilerplate.dto.mahasiswa;

import com.skpijtk.springboot_boilerplate.dto.attendance.AttendanceResponse;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class MahasiswaResponse {
    private Long studentId;
    private Long userId;
    private String studentName;
    private String nim;
    private String email;
    private List<AttendanceResponse> attendanceData;

    public static MahasiswaResponse from(Student student, List<Attendance> attendances) {
        return MahasiswaResponse.builder()
                .studentId(student.getStudentId())
                .userId(student.getUser().getUserId())
                .studentName(student.getUser().getName())
                .nim(student.getNim())
                .email(student.getEmail())
                .attendanceData(attendances.stream().map(AttendanceResponse::from).collect(Collectors.toList()))
                .build();
    }
}
