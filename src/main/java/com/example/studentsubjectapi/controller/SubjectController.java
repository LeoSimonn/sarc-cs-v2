package com.example.studentsubjectapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.service.SubjectService;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Subject> registerSubject(@RequestBody Subject subject) {
        Subject createdSubject = subjectService.addSubject(subject);
        return ResponseEntity.ok(createdSubject);
    }

    @GetMapping("/{subjectCode}")
    public ResponseEntity<Subject> getSubjectByCode(@PathVariable String subjectCode) {
        Subject subject = subjectService.getSubjectByCode(subjectCode);
        if (subject != null) {
            return ResponseEntity.ok(subject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Subject>> getSubjectsByName(@RequestParam String name) {
        List<Subject> subjects = subjectService.getSubjectsByName(name);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }
}