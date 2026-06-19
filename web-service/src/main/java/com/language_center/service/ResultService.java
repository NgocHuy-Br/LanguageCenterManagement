package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.ClassStudent;
import com.language_center.entity.Result;

import com.language_center.repository.ClassStudentRepository;
import com.language_center.repository.ResultRepository;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    private final ClassStudentRepository classStudentRepository;

    public ResultService(
            ResultRepository resultRepository,
            ClassStudentRepository classStudentRepository) {

        this.resultRepository = resultRepository;

        this.classStudentRepository = classStudentRepository;

    }

    // giáo viên nhập điểm

    public Result create(
            Long classStudentId,
            Result result) {

        ClassStudent classStudent = classStudentRepository
                .findById(classStudentId)
                .orElse(null);

        if (classStudent == null) {

            return null;

        }

        result.setClassStudent(classStudent);

        return resultRepository.save(result);

    }

    // lấy toàn bộ kết quả

    public List<Result> getAll() {

        return resultRepository.findAll();

    }

    // học viên xem điểm

    public List<Result> getByStudent(
            Long studentId) {

        return resultRepository
                .findAll()
                .stream()
                .filter(
                        r -> r.getClassStudent()
                                .getStudent()
                                .getId()
                                .equals(studentId))
                .toList();

    }

    // cập nhật điểm

    public Result update(
            Long id,
            Result result) {

        Result old = resultRepository
                .findById(id)
                .orElse(null);

        if (old == null) {

            return null;

        }

        old.setScore(
                result.getScore());

        old.setComment(
                result.getComment());

        return resultRepository.save(old);

    }

    // xóa kết quả

    public boolean delete(Long id) {

        if (!resultRepository.existsById(id)) {

            return false;

        }

        resultRepository.deleteById(id);

        return true;

    }

}