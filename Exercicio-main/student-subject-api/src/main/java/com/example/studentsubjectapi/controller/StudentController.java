package com.example.studentsubjectapi.controller;

import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        Student registeredStudent = studentService.addStudent(student);
        return ResponseEntity.ok(registeredStudent);
    }

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<Student> getStudentByRegistrationNumber(@PathVariable String registrationNumber) {
        Student student = studentService.getStudentByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> getStudentsByName(@RequestParam String name) {
        List<Student> students = studentService.getStudentsByName(name);
        return ResponseEntity.ok(students);
    }
}