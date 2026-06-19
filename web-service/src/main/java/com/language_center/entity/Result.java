package com.language_center.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kết quả của một học viên trong một lớp

    @ManyToOne
    @JoinColumn(name = "class_student_id")
    private ClassStudent classStudent;

    private Double score;

    private String comment;

    public Result() {

    }

    public Long getId() {
        return id;
    }

    public ClassStudent getClassStudent() {
        return classStudent;
    }

    public void setClassStudent(ClassStudent classStudent) {
        this.classStudent = classStudent;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}