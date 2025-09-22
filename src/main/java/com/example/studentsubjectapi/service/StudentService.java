package com.example.studentsubjectapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }
    
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentByRegistrationNumber(String registrationNumber) {
        Student student = studentRepository.findByRegistrationNumber(registrationNumber);
        return Optional.ofNullable(student);
    }

    public List<Student> getStudentsByName(String name) {
        return studentRepository.findByNameContaining(name);
    }
}