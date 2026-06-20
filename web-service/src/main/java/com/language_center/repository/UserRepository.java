package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUsernameIgnoreCase(String username);

    User findByTeacherId(Long teacherId);

    User findByStudentId(Long studentId);

}