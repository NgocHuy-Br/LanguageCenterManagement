package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.Classroom;

public interface ClassroomRepository
        extends JpaRepository<Classroom, Long> {

    java.util.List<Classroom> findByTeacherId(Long teacherId);

    boolean existsByIdAndTeacherId(Long id, Long teacherId);

}