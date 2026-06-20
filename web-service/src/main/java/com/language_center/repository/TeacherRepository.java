package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.Teacher;

public interface TeacherRepository
                extends JpaRepository<Teacher, Long> {

        boolean existsByTeacherIdIgnoreCase(String teacherId);

        boolean existsByTeacherIdIgnoreCaseAndIdNot(String teacherId, Long id);

        Teacher findByTeacherIdIgnoreCase(String teacherId);

}