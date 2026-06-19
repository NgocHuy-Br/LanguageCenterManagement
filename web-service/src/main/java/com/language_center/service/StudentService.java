package com.language_center.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.language_center.entity.Student;
import com.language_center.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(
            StudentRepository repository) {

        this.repository = repository;

    }

    public List<Student> getAll() {

        return repository.findAll();

    }

    public Student create(Student student) {

        return repository.save(student);

    }

    public Student update(
            Long id,
            Student student) {

        Student oldStudent = repository.findById(id)
                .orElse(null);

        if (oldStudent == null) {

            return null;

        }

        oldStudent.setName(
                student.getName());

        oldStudent.setPhone(
                student.getPhone());

        oldStudent.setEmail(
                student.getEmail());

        return repository.save(oldStudent);

    }

    public boolean delete(Long id) {

        if (!repository.existsById(id)) {

            return false;

        }

        repository.deleteById(id);

        return true;

    }

}