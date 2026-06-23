package com.language_center.service;

import java.util.List;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.language_center.entity.Classroom;
import com.language_center.entity.Teacher;

import com.language_center.repository.ClassroomRepository;
import com.language_center.repository.TeacherRepository;
import com.language_center.repository.ClassStudentRepository;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    private final TeacherRepository teacherRepository;

    private final ClassStudentRepository classStudentRepository;

    public ClassroomService(
            ClassroomRepository classroomRepository,
            TeacherRepository teacherRepository,
            ClassStudentRepository classStudentRepository) {

        this.classroomRepository = classroomRepository;

        this.teacherRepository = teacherRepository;

        this.classStudentRepository = classStudentRepository;

    }

    // Lấy tất cả lớp

    public List<Classroom> getAll() {

        return classroomRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        Classroom::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

    }

    public List<Classroom> getByTeacherId(Long teacherId) {

        return classroomRepository.findByTeacherId(teacherId).stream()
                .sorted(Comparator.comparing(
                        Classroom::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

    }

    // Thêm lớp

    public Classroom create(
            Classroom classroom,
            Long teacherId) {

        Teacher teacher = null;

        if (teacherId != null) {
            teacher = teacherRepository
                    .findById(teacherId)
                    .orElse(null);

            if (teacher == null) {

                return null;

            }
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

        Teacher teacher = null;

        if (teacherId != null) {
            teacher = teacherRepository
                    .findById(teacherId)
                    .orElse(null);

            if (teacher == null) {

                return null;

            }
        }

        oldClass.setName(
                classroom.getName());

        oldClass.setTeacher(teacher);

        return classroomRepository.save(oldClass);

    }

    // Xóa lớp

    public boolean delete(Long id) {

        Classroom classroom = classroomRepository.findById(id).orElse(null);

        if (classroom == null) {

            return false;

        }

        if (classroom.getTeacher() != null) {
            throw new IllegalStateException("Không thể xóa lớp đã được gán giáo viên.");
        }

        if (classStudentRepository.existsByClassroomId(id)) {
            throw new IllegalStateException("Không thể xóa lớp đã có học viên.");
        }

        classroomRepository.deleteById(id);

        return true;

    }

}