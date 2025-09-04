package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Subject;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SubjectRepository {

    private final List<Subject> subjects = new ArrayList<>();

    public Subject save(Subject subject) {
        subjects.add(subject);
        return subject;
    }

    public Optional<Subject> findById(String subjectCode) {
        return subjects.stream()
                .filter(subject -> subject.getSubjectCode().equals(subjectCode))
                .findFirst();
    }

    public List<Subject> findAll() {
        return new ArrayList<>(subjects);
    }
}