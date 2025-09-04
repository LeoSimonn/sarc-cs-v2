package com.example.studentsubjectapi.service;

import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject addSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void enrollStudentInSubject(Long studentId, Long subjectId) {
        // Logic to enroll a student in a subject
    }
}