package com.language_center.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.Result;
import com.language_center.service.ResultService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ResultController {

    private final ResultService service;

    public ResultController(
            ResultService service) {

        this.service = service;

    }

    /*
     * Giáo viên nhập kết quả
     * 
     * POST:
     * 
     * /api/teacher/results?classStudentId=1
     * 
     */

    @PostMapping("/teacher/results")
    public ResponseEntity<?> create(

            @RequestParam Long classStudentId,

            @RequestBody Result result

    ) {

        Result data = service.create(
                classStudentId,
                result);

        if (data == null) {

            return ResponseEntity
                    .status(404)
                    .body(

                            new ApiResponse<>(
                                    404,
                                    "Không tìm thấy học viên trong lớp",
                                    null)

                    );

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Nhập kết quả thành công",
                        data)

        );

    }

    /*
     * Xem tất cả kết quả
     * 
     */

    @GetMapping("/admin/results")
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Danh sách kết quả",
                        service.getAll())

        );

    }

    /*
     * Học viên xem kết quả
     * 
     * GET:
     * 
     * /api/student/results/1
     * 
     */

    @GetMapping("/student/results/{studentId}")
    public ResponseEntity<?> getStudentResult(

            @PathVariable Long studentId

    ) {

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Lấy kết quả học tập",
                        service.getByStudent(studentId))

        );

    }

    /*
     * Cập nhật điểm
     * 
     * PUT:
     * 
     * /api/teacher/results/{id}
     * 
     */

    @PutMapping("/teacher/results/{id}")
    public ResponseEntity<?> update(

            @PathVariable Long id,

            @RequestBody Result result

    ) {

        Result data = service.update(
                id,
                result);

        if (data == null) {

            return ResponseEntity
                    .status(404)
                    .body(

                            new ApiResponse<>(
                                    404,
                                    "Không tìm thấy kết quả",
                                    null)

                    );

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Cập nhật kết quả thành công",
                        data)

        );

    }

    /*
     * Xóa kết quả
     * 
     */

    @DeleteMapping("/teacher/results/{id}")
    public ResponseEntity<?> delete(

            @PathVariable Long id

    ) {

        boolean result = service.delete(id);

        if (!result) {

            return ResponseEntity
                    .status(404)
                    .body(

                            new ApiResponse<>(
                                    404,
                                    "Không tìm thấy kết quả",
                                    null)

                    );

        }

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Xóa kết quả thành công",
                        null)

        );

    }

}