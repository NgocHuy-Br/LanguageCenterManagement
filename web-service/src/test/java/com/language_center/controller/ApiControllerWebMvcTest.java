package com.language_center.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import com.language_center.config.SecurityConfig;
import com.language_center.entity.ClassStudent;
import com.language_center.entity.Classroom;
import com.language_center.entity.Result;
import com.language_center.entity.Student;
import com.language_center.entity.Teacher;
import com.language_center.service.ClassStudentService;
import com.language_center.service.ClassroomService;
import com.language_center.service.ResultService;
import com.language_center.service.StudentService;
import com.language_center.service.TeacherService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        AuthController.class,
        StudentController.class,
        TeacherController.class,
        ClassroomController.class,
        ClassStudentController.class,
        ResultController.class
})
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class ApiControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private ClassroomService classroomService;

    @MockitoBean
    private ClassStudentService classStudentService;

    @MockitoBean
    private ResultService resultService;

    @Test
    void login_shouldPermitAllWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/login"))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void adminEndpoint_shouldRejectWrongRole() throws Exception {
        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void studentGetAll_shouldReturn200() throws Exception {
        Student student = new Student();
        student.setName("Nguyen Van A");
        student.setPhone("0900000001");
        student.setEmail("a@student.com");
        when(studentService.getAll()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Nguyen Van A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void studentCreate_shouldReturn200() throws Exception {
        Student saved = new Student();
        saved.setName("Nguyen Van B");
        saved.setPhone("0900000002");
        saved.setEmail("b@student.com");
        when(studentService.create(any(Student.class))).thenReturn(saved);

        Student payload = new Student();
        payload.setName("Nguyen Van B");
        payload.setPhone("0900000002");
        payload.setEmail("b@student.com");

        mockMvc.perform(post("/api/admin/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.email").value("b@student.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void studentUpdateNotFound_shouldReturn404() throws Exception {
        when(studentService.update(eq(99L), any(Student.class))).thenReturn(null);

        Student payload = new Student();
        payload.setName("Not Found");

        mockMvc.perform(put("/api/admin/students/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void studentDelete_shouldReturn200() throws Exception {
        when(studentService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void teacherGetAll_shouldReturn200() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setName("Teacher A");
        teacher.setPhone("0911000000");
        teacher.setEmail("teacher.a@mail.com");
        when(teacherService.getAll()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/api/admin/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Teacher A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void teacherCreate_shouldReturn200() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setName("Teacher B");
        when(teacherService.create(any(Teacher.class))).thenReturn(teacher);

        mockMvc.perform(post("/api/admin/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Teacher B\",\"phone\":\"0911000001\",\"email\":\"b@mail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void teacherUpdate_shouldReturn200() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setName("Teacher Updated");
        when(teacherService.update(eq(1L), any(Teacher.class))).thenReturn(teacher);

        mockMvc.perform(put("/api/admin/teachers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Teacher Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Teacher Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void teacherDeleteNotFound_shouldReturn404() throws Exception {
        when(teacherService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/teachers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classroomGetAll_shouldReturn200() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setName("IELTS01");
        when(classroomService.getAll()).thenReturn(List.of(classroom));

        mockMvc.perform(get("/api/admin/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].name").value("IELTS01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classroomCreate_shouldReturn200() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setName("TOEIC01");
        when(classroomService.create(any(Classroom.class), eq(1L))).thenReturn(classroom);

        mockMvc.perform(post("/api/admin/classes")
                .param("teacherId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TOEIC01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("TOEIC01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classroomUpdateNotFound_shouldReturn404() throws Exception {
        when(classroomService.update(eq(77L), any(Classroom.class), eq(1L))).thenReturn(null);

        mockMvc.perform(put("/api/admin/classes/77")
                .param("teacherId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"N/A\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classroomDelete_shouldReturn200() throws Exception {
        when(classroomService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classStudentAdd_shouldReturn200() throws Exception {
        ClassStudent classStudent = new ClassStudent();
        when(classStudentService.addStudent(1L, 2L)).thenReturn(classStudent);

        mockMvc.perform(post("/api/admin/class-students/add")
                .param("classroomId", "1")
                .param("studentId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classStudentGetAll_shouldReturn200() throws Exception {
        when(classStudentService.getAll()).thenReturn(List.of(new ClassStudent()));

        mockMvc.perform(get("/api/admin/class-students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void classStudentDeleteNotFound_shouldReturn404() throws Exception {
        when(classStudentService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/class-students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void resultCreate_shouldReturn200() throws Exception {
        Result result = new Result();
        result.setScore(8.5);
        result.setComment("Good");
        when(resultService.create(eq(1L), any(Result.class))).thenReturn(result);

        mockMvc.perform(post("/api/teacher/results")
                .param("classStudentId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\":8.5,\"comment\":\"Good\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(8.5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resultGetAll_shouldReturn200() throws Exception {
        Result result = new Result();
        result.setScore(9.0);
        when(resultService.getAll()).thenReturn(List.of(result));

        mockMvc.perform(get("/api/admin/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void resultGetByStudent_shouldReturn200() throws Exception {
        Result result = new Result();
        result.setScore(7.5);
        when(resultService.getByStudent(1L)).thenReturn(List.of(result));

        mockMvc.perform(get("/api/student/results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void resultUpdate_shouldReturn200() throws Exception {
        Result result = new Result();
        result.setScore(9.5);
        result.setComment("Excellent");
        when(resultService.update(eq(10L), any(Result.class))).thenReturn(result);

        mockMvc.perform(put("/api/teacher/results/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\":9.5,\"comment\":\"Excellent\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment").value("Excellent"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void resultDeleteNotFound_shouldReturn404() throws Exception {
        when(resultService.delete(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/teacher/results/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
