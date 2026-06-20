package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.Student;

public interface StudentRepository
                extends JpaRepository<Student, Long> {

        boolean existsByStudentIdIgnoreCase(String studentId);

        boolean existsByStudentIdIgnoreCaseAndIdNot(String studentId, Long id);

        Student findByStudentIdIgnoreCase(String studentId);

}