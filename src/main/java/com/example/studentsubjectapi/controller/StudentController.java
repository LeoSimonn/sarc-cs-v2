package com.example.studentsubjectapi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // LISTAR TODOS (corrige o 405 do GET /students)
    @GetMapping
    public ResponseEntity<List<Student>> listAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping
    public ResponseEntity<Student> registerStudent(@Valid @RequestBody Student student) {
        Student registeredStudent = studentService.addStudent(student);
        return ResponseEntity.ok(registeredStudent);
    }

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<Student> getStudentByRegistrationNumber(@PathVariable String registrationNumber) {
        Optional<Student> student = studentService.getStudentByRegistrationNumber(registrationNumber);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> getStudentsByName(@RequestParam String name) {
        List<Student> students = studentService.getStudentsByName(name);
        return ResponseEntity.ok(students);
    }
}