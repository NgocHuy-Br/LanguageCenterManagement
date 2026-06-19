package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.Teacher;
import com.language_center.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(
            TeacherRepository teacherRepository) {

        this.teacherRepository = teacherRepository;

    }

    // lấy tất cả giáo viên
    public List<Teacher> getAll() {

        return teacherRepository.findAll();

    }

    // thêm giáo viên
    public Teacher create(Teacher teacher) {

        return teacherRepository.save(teacher);

    }

    // cập nhật
    public Teacher update(
            Long id,
            Teacher teacher) {

        Teacher oldTeacher = teacherRepository
                .findById(id)
                .orElse(null);

        if (oldTeacher == null) {

            return null;

        }

        oldTeacher.setName(
                teacher.getName());

        oldTeacher.setPhone(
                teacher.getPhone());

        oldTeacher.setEmail(
                teacher.getEmail());

        return teacherRepository.save(oldTeacher);

    }

    // xóa

    public boolean delete(Long id) {

        if (!teacherRepository.existsById(id)) {

            return false;

        }

        teacherRepository.deleteById(id);

        return true;

    }

}