package com.language_center.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.ClassStudent;
import com.language_center.service.ClassStudentService;

@RestController
@RequestMapping("/api/admin/class-students")
@CrossOrigin("*")
public class ClassStudentController {

    private final ClassStudentService service;

    public ClassStudentController(
            ClassStudentService service) {

        this.service = service;

    }

    /*
     * Thêm học viên vào lớp
     * 
     * POST:
     * 
     * /api/admin/class-students/add?classroomId=1&studentId=2
     * 
     */

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestParam Long classroomId,
            @RequestParam Long studentId) {

        ClassStudent result = service.addStudent(
                classroomId,
                studentId);

        if (result == null) {

            return ResponseEntity
                    .status(404)
                    .body(
                            new ApiResponse<>(
                                    404,
                                    "Không tìm thấy lớp hoặc học viên",
                                    null));

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Thêm học viên vào lớp thành công",
                        result)

        );

    }

    /*
     * Xem danh sách phân lớp
     * 
     */

    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Lấy danh sách phân lớp thành công",
                        service.getAll())

        );

    }

    /*
     * Xóa học viên khỏi lớp
     * 
     */

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
                                    "Không tìm thấy dữ liệu phân lớp",
                                    null));

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Xóa học viên khỏi lớp thành công",
                        null)

        );

    }

}