package com.language_center.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.Student;
import com.language_center.service.StudentService;

@RestController
@RequestMapping("/api/admin/students")
@CrossOrigin("*")
public class StudentController {

        private final StudentService service;

        public StudentController(
                        StudentService service) {

                this.service = service;

        }

        @GetMapping
        public ResponseEntity<?> getAll() {

                return ResponseEntity.ok(

                                new ApiResponse<>(
                                                200,
                                                "Lấy danh sách học viên thành công",
                                                service.getAll())

                );

        }

        @PostMapping
        public ResponseEntity<?> create(
                        @RequestBody Student student) {

                try {
                        Student result = service.create(student);

                        return ResponseEntity.ok(

                                        new ApiResponse<>(
                                                        200,
                                                        "Thêm học viên thành công",
                                                        result)

                        );
                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest()
                                        .body(new ApiResponse<>(400, ex.getMessage(), null));
                }

        }

        @PutMapping("/{id}")
        public ResponseEntity<?> update(
                        @PathVariable Long id,
                        @RequestBody Student student) {

                try {
                        Student result = service.update(id, student);

                        if (result == null) {

                                return ResponseEntity
                                                .status(404)
                                                .body(

                                                                new ApiResponse<>(
                                                                                404,
                                                                                "Không tìm thấy học viên",
                                                                                null)

                                                );

                        }

                        return ResponseEntity.ok(

                                        new ApiResponse<>(
                                                        200,
                                                        "Cập nhật học viên thành công",
                                                        result)

                        );
                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest()
                                        .body(new ApiResponse<>(400, ex.getMessage(), null));
                }

        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(
                        @PathVariable Long id) {

                boolean result = service.delete(id);

                if (!result) {

                        return ResponseEntity
                                        .status(404)
                                        .body(

                                                        new ApiResponse<>(
                                                                        404,
                                                                        "Không tìm thấy học viên",
                                                                        null)

                                        );

                }

                return ResponseEntity.ok(

                                new ApiResponse<>(
                                                200,
                                                "Xóa học viên thành công",
                                                null)

                );

        }

}