package com.example.studentsubjectapi.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_id"})
})
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    public Enrollment() {
    }
    
    public Enrollment(Student student, Subject subject) {
        this.student = student;
        this.subject = subject;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Subject getSubject() {
        return subject;
    }
    
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
