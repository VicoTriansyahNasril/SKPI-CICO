package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {
    long countByAttendanceDateAndCheckInTimeIsNotNull(LocalDate date);
    long countByAttendanceDateAndIsLateTrue(LocalDate date);
    List<Attendance> findByStudentStudentId(Long studentId);
}
