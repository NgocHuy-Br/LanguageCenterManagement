package com.language_center.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.repository.StudentRepository;
import com.language_center.repository.TeacherRepository;
import com.language_center.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public AuthController(
            UserService userService,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository) {

        this.userService = userService;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login() {

        return ResponseEntity.ok(
                "Login thành công");

    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {

        var user = userService.getByUsername(principal.getName());

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(404, "Không tìm thấy người dùng", null));
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("role", user.getRole());
        profile.put("teacherId", user.getTeacherId());
        profile.put("studentId", user.getStudentId());

        if (user.getTeacherId() != null) {
            var teacher = teacherRepository.findById(user.getTeacherId()).orElse(null);
            profile.put("teacher", teacher);
        } else {
            profile.put("teacher", null);
        }

        if (user.getStudentId() != null) {
            var student = studentRepository.findById(user.getStudentId()).orElse(null);
            profile.put("student", student);
        } else {
            profile.put("student", null);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy thông tin đăng nhập thành công", profile));

    }

}