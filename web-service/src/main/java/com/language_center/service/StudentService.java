package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.Student;
import com.language_center.entity.Teacher;
import com.language_center.repository.StudentRepository;
import com.language_center.repository.TeacherRepository;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final TeacherRepository teacherRepository;
    private final UserService userService;

    public StudentService(
            StudentRepository repository,
            TeacherRepository teacherRepository,
            UserService userService) {

        this.repository = repository;
        this.teacherRepository = teacherRepository;
        this.userService = userService;

    }

    public List<Student> getAll() {

        return repository.findAll();

    }

    public Student create(Student student) {

        validateStudentForCreate(student);

        Student saved = repository.save(student);

        userService.upsertStudentUser(saved);

        return saved;

    }

    public Student update(
            Long id,
            Student student) {

        Student oldStudent = repository.findById(id)
                .orElse(null);

        if (oldStudent == null) {

            return null;

        }

        validateStudentForUpdate(id, student);

        oldStudent.setStudentId(
                normalizeId(student.getStudentId()));

        oldStudent.setName(
                student.getName());

        oldStudent.setBirthDate(
                student.getBirthDate());

        oldStudent.setAddress(
                student.getAddress());

        oldStudent.setPhone(
                student.getPhone());

        oldStudent.setEmail(
                student.getEmail());

        Student saved = repository.save(oldStudent);

        userService.upsertStudentUser(saved);

        return saved;

    }

    public boolean delete(Long id) {

        if (!repository.existsById(id)) {

            return false;

        }

        userService.deleteStudentUser(id);

        repository.deleteById(id);

        return true;

    }

    private void validateStudentForCreate(Student student) {

        String studentId = normalizeId(student == null ? null : student.getStudentId());

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Mã số học viên không được để trống.");
        }

        if (repository.existsByStudentIdIgnoreCase(studentId)) {
            throw new IllegalArgumentException("Mã số học viên đã tồn tại.");
        }

        if (teacherRepository.existsByTeacherIdIgnoreCase(studentId)) {
            throw new IllegalArgumentException("Mã số đã được dùng cho giáo viên.");
        }

        student.setStudentId(studentId);

    }

    private void validateStudentForUpdate(Long id, Student student) {

        String studentId = normalizeId(student == null ? null : student.getStudentId());

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Mã số học viên không được để trống.");
        }

        if (repository.existsByStudentIdIgnoreCaseAndIdNot(studentId, id)) {
            throw new IllegalArgumentException("Mã số học viên đã tồn tại.");
        }

        Teacher conflictTeacher = teacherRepository.findByTeacherIdIgnoreCase(studentId);
        if (conflictTeacher != null) {
            throw new IllegalArgumentException("Mã số đã được dùng cho giáo viên.");
        }

    }

    private String normalizeId(String id) {

        return id == null ? null : id.trim();

    }

}