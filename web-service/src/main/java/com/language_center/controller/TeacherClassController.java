package com.language_center.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.language_center.dto.ApiResponse;
import com.language_center.entity.ClassStudent;
import com.language_center.entity.Classroom;
import com.language_center.service.ClassStudentService;
import com.language_center.service.ClassroomService;
import com.language_center.service.UserService;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin("*")
public class TeacherClassController {

    private final ClassroomService classroomService;
    private final ClassStudentService classStudentService;
    private final UserService userService;

    public TeacherClassController(
            ClassroomService classroomService,
            ClassStudentService classStudentService,
            UserService userService) {

        this.classroomService = classroomService;
        this.classStudentService = classStudentService;
        this.userService = userService;

    }

    /*
     * Giáo viên xem danh sách tất cả lớp học
     *
     * GET /api/teacher/classes
     */

    @GetMapping("/classes")
    public ResponseEntity<?> getAllClasses(Principal principal) {

        Long teacherId = userService.getTeacherIdByUsername(principal.getName());

        List<Classroom> classes = teacherId == null
                ? List.of()
                : classroomService.getByTeacherId(teacherId);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Danh sách lớp học", classes));

    }

    /*
     * Giáo viên xem danh sách học viên trong một lớp
     *
     * GET /api/teacher/class-students?classroomId=1
     */

    @GetMapping("/class-students")
    public ResponseEntity<?> getClassStudents(
            Principal principal,
            @RequestParam Long classroomId) {

        Long teacherId = userService.getTeacherIdByUsername(principal.getName());

        List<ClassStudent> list = teacherId == null
                ? List.of()
                : classroomService.getByTeacherId(teacherId)
                        .stream()
                        .anyMatch(classroom -> classroom.getId().equals(classroomId))
                                ? classStudentService.getByClassroomId(classroomId)
                                : List.of();

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Danh sách học viên trong lớp", list));

    }

}
