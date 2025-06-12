package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
