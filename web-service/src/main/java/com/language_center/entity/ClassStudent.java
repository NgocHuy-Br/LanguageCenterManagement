package com.language_center.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "class_student")
public class ClassStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Một phân lớp thuộc một lớp
     */
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    /*
     * Một phân lớp thuộc một học viên
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public ClassStudent() {

    }

    public Long getId() {
        return id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}