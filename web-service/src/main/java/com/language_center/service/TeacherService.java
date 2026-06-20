package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.Teacher;
import com.language_center.repository.StudentRepository;
import com.language_center.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final UserService userService;

    public TeacherService(
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            UserService userService) {

        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.userService = userService;

    }

    // lấy tất cả giáo viên
    public List<Teacher> getAll() {

        return teacherRepository.findAll();

    }

    // thêm giáo viên
    public Teacher create(Teacher teacher) {

        validateTeacherForCreate(teacher);

        Teacher saved = teacherRepository.save(teacher);

        userService.upsertTeacherUser(saved);

        return saved;

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

        validateTeacherForUpdate(id, teacher);

        oldTeacher.setTeacherId(
                normalizeId(teacher.getTeacherId()));

        oldTeacher.setName(
                teacher.getName());

        oldTeacher.setBirthDate(
                teacher.getBirthDate());

        oldTeacher.setAddress(
                teacher.getAddress());

        oldTeacher.setPhone(
                teacher.getPhone());

        oldTeacher.setEmail(
                teacher.getEmail());

        Teacher saved = teacherRepository.save(oldTeacher);

        userService.upsertTeacherUser(saved);

        return saved;

    }

    // xóa

    public boolean delete(Long id) {

        if (!teacherRepository.existsById(id)) {

            return false;

        }

        userService.deleteTeacherUser(id);

        teacherRepository.deleteById(id);

        return true;

    }

    private void validateTeacherForCreate(Teacher teacher) {

        String teacherId = normalizeId(teacher == null ? null : teacher.getTeacherId());

        if (teacherId == null || teacherId.isBlank()) {
            throw new IllegalArgumentException("Mã số giáo viên không được để trống.");
        }

        if (teacherRepository.existsByTeacherIdIgnoreCase(teacherId)) {
            throw new IllegalArgumentException("Mã số giáo viên đã tồn tại.");
        }

        if (studentRepository.existsByStudentIdIgnoreCase(teacherId)) {
            throw new IllegalArgumentException("Mã số đã được dùng cho học viên.");
        }

        teacher.setTeacherId(teacherId);

    }

    private void validateTeacherForUpdate(Long id, Teacher teacher) {

        String teacherId = normalizeId(teacher == null ? null : teacher.getTeacherId());

        if (teacherId == null || teacherId.isBlank()) {
            throw new IllegalArgumentException("Mã số giáo viên không được để trống.");
        }

        if (teacherRepository.existsByTeacherIdIgnoreCaseAndIdNot(teacherId, id)) {
            throw new IllegalArgumentException("Mã số giáo viên đã tồn tại.");
        }

        if (studentRepository.existsByStudentIdIgnoreCase(teacherId)) {
            throw new IllegalArgumentException("Mã số đã được dùng cho học viên.");
        }

    }

    private String normalizeId(String id) {

        return id == null ? null : id.trim();

    }

}