package com.skpijtk.springboot_boilerplate.specification;

import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.Student;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceSpecification {

    public static Specification<Attendance> filterByCriteria(String studentName, String nim, LocalDate startDate, LocalDate endDate) {
        return (Root<Attendance> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Attendance, Student> studentJoin = root.join("student", JoinType.INNER);

            if (studentName != null && !studentName.trim().isEmpty()) {
                Expression<String> fullName = cb.concat(cb.lower(studentJoin.get("firstName")), " ");
                fullName = cb.concat(fullName, cb.lower(studentJoin.get("lastName")));
                predicates.add(cb.like(fullName, "%" + studentName.toLowerCase() + "%"));
            }

            if (nim != null && !nim.trim().isEmpty()) {
                predicates.add(cb.like(studentJoin.get("nim"), nim + "%"));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
