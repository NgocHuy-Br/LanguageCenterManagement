package com.language_center.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.Classroom;
import com.language_center.service.ClassroomService;

@RestController
@RequestMapping("/api/admin/classes")
@CrossOrigin("*")
public class ClassroomController {

    private final ClassroomService service;

    public ClassroomController(
            ClassroomService service) {

        this.service = service;

    }

    /*
     * Lấy danh sách lớp
     * 
     * GET:
     * /api/admin/classes
     */

    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Lấy danh sách lớp thành công",
                        service.getAll())

        );

    }

    /*
     * Tạo lớp
     * 
     * POST:
     * /api/admin/classes?teacherId=1
     * 
     * Body:
     * 
     * {
     * "name":"IELTS01"
     * }
     * 
     */

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Classroom classroom,
            @RequestParam Long teacherId) {

        Classroom result = service.create(
                classroom,
                teacherId);

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
                        "Tạo lớp thành công",
                        result)

        );

    }

    /*
     * Cập nhật lớp
     * 
     * PUT:
     * 
     * /api/admin/classes/1?teacherId=2
     * 
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Classroom classroom,
            @RequestParam Long teacherId) {

        Classroom result = service.update(
                id,
                classroom,
                teacherId);

        if (result == null) {

            return ResponseEntity
                    .status(404)
                    .body(

                            new ApiResponse<>(
                                    404,
                                    "Không tìm thấy lớp hoặc giáo viên",
                                    null)

                    );

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Cập nhật lớp thành công",
                        result)

        );

    }

    /*
     * Xóa lớp
     * 
     * DELETE:
     * 
     * /api/admin/classes/{id}
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
                                    "Không tìm thấy lớp",
                                    null)

                    );

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Xóa lớp thành công",
                        null)

        );

    }

}