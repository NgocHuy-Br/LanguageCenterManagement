package com.language_center.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.Result;
import com.language_center.service.ResultService;
import com.language_center.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ResultController {

    private final ResultService service;
    private final UserService userService;

    public ResultController(
            ResultService service,
            UserService userService) {

        this.service = service;
        this.userService = userService;

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

            Principal principal,

            @RequestParam Long classStudentId,

            @RequestBody Result result

    ) {

        Long teacherId = userService.getTeacherIdByUsername(principal.getName());

        Result data = service.createForTeacher(
                teacherId,
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

            Principal principal,
            @PathVariable Long studentId

    ) {

        Long currentStudentId = userService.getStudentIdByUsername(principal.getName());

        if (currentStudentId == null || !currentStudentId.equals(studentId)) {
            return ResponseEntity.status(403).body(
                    new ApiResponse<>(403, "Bạn chỉ được xem kết quả của chính mình", null));
        }

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

            Principal principal,

            @PathVariable Long id,

            @RequestBody Result result

    ) {

        Long teacherId = userService.getTeacherIdByUsername(principal.getName());

        Result data = service.updateForTeacher(
                teacherId,
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

            Principal principal,

            @PathVariable Long id

    ) {

        Long teacherId = userService.getTeacherIdByUsername(principal.getName());

        boolean result = service.deleteForTeacher(teacherId, id);

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

    /*
     * Giáo viên xem kết quả theo lớp
     *
     * GET /api/teacher/results?classroomId=1
     */

    @GetMapping("/teacher/results")
    public ResponseEntity<?> getByClassroom(

            Principal principal,

            @RequestParam Long classroomId

    ) {

        Long teacherId = userService.getTeacherIdByUsername(principal.getName());

        return ResponseEntity.ok(

                new ApiResponse<>(
                        200,
                        "Kết quả theo lớp",
                        service.getByClassroomForTeacher(teacherId, classroomId))

        );

    }

}