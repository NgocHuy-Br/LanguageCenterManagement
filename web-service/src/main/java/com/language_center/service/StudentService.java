package com.language_center.service;

import java.util.List;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.language_center.entity.Student;
import com.language_center.entity.Teacher;
import com.language_center.repository.StudentRepository;
import com.language_center.repository.TeacherRepository;
import com.language_center.repository.ClassStudentRepository;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final TeacherRepository teacherRepository;
    private final ClassStudentRepository classStudentRepository;
    private final UserService userService;

    public StudentService(
            StudentRepository repository,
            TeacherRepository teacherRepository,
            ClassStudentRepository classStudentRepository,
            UserService userService) {

        this.repository = repository;
        this.teacherRepository = teacherRepository;
        this.classStudentRepository = classStudentRepository;
        this.userService = userService;

    }

    public List<Student> getAll() {

        return repository.findAll().stream()
                .sorted(Comparator.comparing(
                        Student::getStudentId,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

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

        if (classStudentRepository.existsByStudentId(id)) {
            throw new IllegalStateException("Không thể xóa học viên đã được gán lớp.");
        }

        userService.deleteStudentUser(id);

        repository.deleteById(id);

        return true;

    }

    private void validateStudentForCreate(Student student) {

        if (student == null) {
            throw new IllegalArgumentException("Dữ liệu học viên không hợp lệ.");
        }

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