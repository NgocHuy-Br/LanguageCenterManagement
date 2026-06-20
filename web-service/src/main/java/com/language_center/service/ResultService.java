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

    public Result createForTeacher(
            Long teacherId,
            Long classStudentId,
            Result result) {

        ClassStudent classStudent = classStudentRepository
                .findById(classStudentId)
                .orElse(null);

        if (classStudent == null
                || classStudent.getClassroom() == null
                || classStudent.getClassroom().getTeacher() == null
                || !teacherId.equals(classStudent.getClassroom().getTeacher().getId())) {

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

        return resultRepository.findByClassStudentStudentId(studentId);

    }

    public List<Result> getByStudentUsername(Long studentId) {

        return resultRepository.findByClassStudentStudentId(studentId);

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

    public Result updateForTeacher(
            Long teacherId,
            Long id,
            Result result) {

        Result old = resultRepository
                .findById(id)
                .orElse(null);

        if (old == null
                || old.getClassStudent() == null
                || old.getClassStudent().getClassroom() == null
                || old.getClassStudent().getClassroom().getTeacher() == null
                || !teacherId.equals(old.getClassStudent().getClassroom().getTeacher().getId())) {

            return null;

        }

        old.setScore(result.getScore());
        old.setComment(result.getComment());

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

    public boolean deleteForTeacher(Long teacherId, Long id) {

        Result result = resultRepository.findById(id).orElse(null);

        if (result == null
                || result.getClassStudent() == null
                || result.getClassStudent().getClassroom() == null
                || result.getClassStudent().getClassroom().getTeacher() == null
                || !teacherId.equals(result.getClassStudent().getClassroom().getTeacher().getId())) {

            return false;

        }

        resultRepository.deleteById(id);

        return true;

    }

    // lấy kết quả theo lớp (dành cho giáo viên)

    public List<Result> getByClassroom(Long classroomId) {

        return resultRepository.findByClassStudentClassroomId(classroomId);

    }

    public List<Result> getByClassroomForTeacher(Long teacherId, Long classroomId) {

        return resultRepository.findByClassStudentClassroomTeacherIdAndClassStudentClassroomId(teacherId, classroomId);

    }

}