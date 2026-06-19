package com.language_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.language_center.entity.Result;

public interface ResultRepository
        extends JpaRepository<Result, Long> {

}