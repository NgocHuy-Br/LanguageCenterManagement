package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.ClassStudent;

public interface ClassStudentRepository
                extends JpaRepository<ClassStudent, Long> {

        java.util.List<ClassStudent> findByClassroomId(Long classroomId);

}