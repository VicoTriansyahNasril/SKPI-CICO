package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    long countByAttendanceDateAndCheckInTimeIsNotNull(LocalDate date);
    long countByAttendanceDateAndIsLateTrue(LocalDate date);
}
