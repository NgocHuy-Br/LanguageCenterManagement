package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.ClassStudent;
import com.language_center.entity.Classroom;
import com.language_center.entity.Student;

import com.language_center.repository.ClassStudentRepository;
import com.language_center.repository.ClassroomRepository;
import com.language_center.repository.StudentRepository;

@Service
public class ClassStudentService {

    private final ClassStudentRepository classStudentRepository;

    private final ClassroomRepository classroomRepository;

    private final StudentRepository studentRepository;

    public ClassStudentService(
            ClassStudentRepository classStudentRepository,
            ClassroomRepository classroomRepository,
            StudentRepository studentRepository) {

        this.classStudentRepository = classStudentRepository;
        this.classroomRepository = classroomRepository;
        this.studentRepository = studentRepository;

    }

    // thêm học viên vào lớp

    public ClassStudent addStudent(
            Long classroomId,
            Long studentId) {

        Classroom classroom = classroomRepository
                .findById(classroomId)
                .orElse(null);

        Student student = studentRepository
                .findById(studentId)
                .orElse(null);

        if (classroom == null || student == null) {

            return null;

        }

        ClassStudent cs = new ClassStudent();

        cs.setClassroom(classroom);

        cs.setStudent(student);

        return classStudentRepository.save(cs);

    }

    // lấy tất cả phân lớp

    public List<ClassStudent> getAll() {

        return classStudentRepository.findAll();

    }

    // lấy danh sách học viên theo lớp

    public List<ClassStudent> getByClassroomId(Long classroomId) {

        return classStudentRepository.findByClassroomId(classroomId);

    }

    // xóa học viên khỏi lớp

    public boolean delete(Long id) {

        if (!classStudentRepository.existsById(id)) {

            return false;

        }

        classStudentRepository.deleteById(id);

        return true;

    }

}