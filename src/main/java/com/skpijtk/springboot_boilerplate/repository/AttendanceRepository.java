package com.skpijtk.springboot_boilerplate.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skpijtk.springboot_boilerplate.model.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    long countByAttendanceDateAndCheckInTimeIsNotNull(LocalDate date);
    long countByAttendanceDateAndIsLateTrue(LocalDate date);

    @Query("SELECT a FROM Attendance a " +
       "JOIN FETCH a.student s " +
       "JOIN FETCH s.user u " +
       "WHERE " +
       "(:studentName IS NULL OR LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :studentName, '%'))) " +
       "AND (:nim IS NULL OR s.nim = :nim) " +
       "AND (:startDate IS NULL OR a.attendanceDate >= :startDate) " +
       "AND (:endDate IS NULL OR a.attendanceDate <= :endDate)")
List<Attendance> findFilteredAttendances(
    @Param("studentName") String studentName,
    @Param("nim") String nim,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
);

}
