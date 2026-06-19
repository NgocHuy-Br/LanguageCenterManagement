package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.Classroom;
import com.language_center.entity.Teacher;

import com.language_center.repository.ClassroomRepository;
import com.language_center.repository.TeacherRepository;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    private final TeacherRepository teacherRepository;

    public ClassroomService(
            ClassroomRepository classroomRepository,
            TeacherRepository teacherRepository) {

        this.classroomRepository = classroomRepository;

        this.teacherRepository = teacherRepository;

    }

    // Lấy tất cả lớp

    public List<Classroom> getAll() {

        return classroomRepository.findAll();

    }

    // Thêm lớp

    public Classroom create(
            Classroom classroom,
            Long teacherId) {

        Teacher teacher = teacherRepository
                .findById(teacherId)
                .orElse(null);

        if (teacher == null) {

            return null;

        }

        classroom.setTeacher(teacher);

        return classroomRepository.save(classroom);

    }

    // Cập nhật lớp

    public Classroom update(
            Long id,
            Classroom classroom,
            Long teacherId) {

        Classroom oldClass = classroomRepository
                .findById(id)
                .orElse(null);

        if (oldClass == null) {

            return null;

        }

        Teacher teacher = teacherRepository
                .findById(teacherId)
                .orElse(null);

        if (teacher == null) {

            return null;

        }

        oldClass.setName(
                classroom.getName());

        oldClass.setTeacher(teacher);

        return classroomRepository.save(oldClass);

    }

    // Xóa lớp

    public boolean delete(Long id) {

        if (!classroomRepository.existsById(id)) {

            return false;

        }

        classroomRepository.deleteById(id);

        return true;

    }

}