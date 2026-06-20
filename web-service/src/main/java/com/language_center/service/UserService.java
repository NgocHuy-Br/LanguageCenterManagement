package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.Student;
import com.language_center.entity.Teacher;
import com.language_center.entity.User;
import com.language_center.repository.StudentRepository;
import com.language_center.repository.TeacherRepository;
import com.language_center.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    public static final String DEFAULT_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public UserService(
            UserRepository userRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository) {

        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;

    }

    @PostConstruct
    public void initializeDefaultUsers() {

        ensureAdminAccount();
        syncTeacherAccounts();
        syncStudentAccounts();

    }

    public void ensureAdminAccount() {

        User admin = userRepository.findByUsernameIgnoreCase("admin");

        if (admin == null) {
            admin = new User();
        }

        admin.setUsername("admin");
        admin.setPassword(DEFAULT_PASSWORD);
        admin.setRole("ADMIN");
        admin.setTeacherId(null);
        admin.setStudentId(null);

        userRepository.save(admin);

    }

    public void upsertTeacherUser(Teacher teacher) {

        if (teacher == null || teacher.getId() == null || teacher.getTeacherId() == null
                || teacher.getTeacherId().isBlank()) {
            return;
        }

        User user = userRepository.findByTeacherId(teacher.getId());

        if (user == null) {
            user = new User();
        }

        user.setUsername(teacher.getTeacherId().trim());
        user.setPassword(DEFAULT_PASSWORD);
        user.setRole("TEACHER");
        user.setTeacherId(teacher.getId());
        user.setStudentId(null);

        userRepository.save(user);

    }

    public void upsertStudentUser(Student student) {

        if (student == null || student.getId() == null || student.getStudentId() == null
                || student.getStudentId().isBlank()) {
            return;
        }

        User user = userRepository.findByStudentId(student.getId());

        if (user == null) {
            user = new User();
        }

        user.setUsername(student.getStudentId().trim());
        user.setPassword(DEFAULT_PASSWORD);
        user.setRole("STUDENT");
        user.setTeacherId(null);
        user.setStudentId(student.getId());

        userRepository.save(user);

    }

    public void deleteTeacherUser(Long teacherId) {

        User user = userRepository.findByTeacherId(teacherId);

        if (user != null) {
            userRepository.delete(user);
        }

    }

    public void deleteStudentUser(Long studentId) {

        User user = userRepository.findByStudentId(studentId);

        if (user != null) {
            userRepository.delete(user);
        }

    }

    public Long getTeacherIdByUsername(String username) {

        User user = userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim());

        return user == null ? null : user.getTeacherId();

    }

    public Long getStudentIdByUsername(String username) {

        User user = userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim());

        return user == null ? null : user.getStudentId();

    }

    public User getByUsername(String username) {

        return userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim());

    }

    private void syncTeacherAccounts() {

        List<Teacher> teachers = teacherRepository.findAll();

        for (Teacher teacher : teachers) {
            upsertTeacherUser(teacher);
        }

    }

    private void syncStudentAccounts() {

        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            upsertStudentUser(student);
        }

    }

}
