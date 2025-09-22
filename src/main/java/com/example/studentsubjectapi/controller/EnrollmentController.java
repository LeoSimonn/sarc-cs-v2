package com.example.studentsubjectapi.controller;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<Enrollment> enrollStudent(@RequestParam String studentRegistrationNumber,
                                                   @RequestParam String subjectCode) {
        try {
            Enrollment enrollment = enrollmentService.enrollStudentInSubject(studentRegistrationNumber, subjectCode);
            return ResponseEntity.ok(enrollment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build(); // Conflict
        }
    }

    @GetMapping("/student/{studentRegistrationNumber}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(@PathVariable String studentRegistrationNumber) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentRegistrationNumber);
            return ResponseEntity.ok(enrollments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subject/{subjectCode}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsBySubject(@PathVariable String subjectCode) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySubject(subjectCode);
            return ResponseEntity.ok(enrollments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    @DeleteMapping
    public ResponseEntity<Void> unenrollStudent(@RequestParam String studentRegistrationNumber,
                                               @RequestParam String subjectCode) {
        try {
            enrollmentService.unenrollStudentFromSubject(studentRegistrationNumber, subjectCode);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
