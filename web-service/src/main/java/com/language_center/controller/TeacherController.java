package com.language_center.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.Teacher;
import com.language_center.service.TeacherService;

@RestController
@RequestMapping("/api/admin/teachers")
@CrossOrigin("*")
public class TeacherController {

        private final TeacherService teacherService;

        public TeacherController(
                        TeacherService teacherService) {

                this.teacherService = teacherService;

        }

        // GET ALL

        @GetMapping
        public ResponseEntity<?> getAll() {
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Lấy danh sách giáo viên thành công",
                                                teacherService.getAll()));
        }

        // CREATE
        @PostMapping
        public ResponseEntity<?> create(
                        @RequestBody Teacher teacher) {

                try {
                        Teacher result = teacherService.create(teacher);

                        return ResponseEntity.ok(

                                        new ApiResponse<>(
                                                        200,
                                                        "Thêm giáo viên thành công",
                                                        result));
                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest()
                                        .body(new ApiResponse<>(400, ex.getMessage(), null));
                }

        }

        // UPDATE
        @PutMapping("/{id}")
        public ResponseEntity<?> update(
                        @PathVariable Long id,
                        @RequestBody Teacher teacher) {

                try {
                        Teacher result = teacherService.update(id, teacher);

                        if (result == null) {

                                return ResponseEntity
                                                .status(404)
                                                .body(

                                                                new ApiResponse<>(
                                                                                404,
                                                                                "Không tìm thấy giáo viên",
                                                                                null)

                                                );

                        }
                        return ResponseEntity.ok(

                                        new ApiResponse<>(
                                                        200,
                                                        "Cập nhật giáo viên thành công",
                                                        result));
                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest()
                                        .body(new ApiResponse<>(400, ex.getMessage(), null));
                }

        }

        // DELETE

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(
                        @PathVariable Long id) {

                boolean result = teacherService.delete(id);

                if (!result) {

                        return ResponseEntity
                                        .status(404)
                                        .body(

                                                        new ApiResponse<>(
                                                                        404,
                                                                        "Không tìm thấy giáo viên",
                                                                        null)

                                        );

                }

                return ResponseEntity.ok(

                                new ApiResponse<>(
                                                200,
                                                "Xóa giáo viên thành công",
                                                null)

                );

        }

}