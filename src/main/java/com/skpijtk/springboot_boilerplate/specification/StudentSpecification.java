package com.skpijtk.springboot_boilerplate.specification;

import com.skpijtk.springboot_boilerplate.model.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {
    public static Specification<Student> byName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("user").get("name")),
                "%" + name.toLowerCase() + "%"
            );
        };
    }
}
