package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.Result;

public interface ResultRepository
        extends JpaRepository<Result, Long> {

    java.util.List<Result> findByClassStudentClassroomId(Long classroomId);

    java.util.List<Result> findByClassStudentStudentId(Long studentId);

    java.util.List<Result> findByClassStudentClassroomTeacherIdAndClassStudentClassroomId(Long teacherId,
            Long classroomId);

}