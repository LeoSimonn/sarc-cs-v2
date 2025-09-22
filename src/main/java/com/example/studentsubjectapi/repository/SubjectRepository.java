package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findBySubjectCode(String subjectCode);
    List<Subject> findBySubjectNameContaining(String subjectName);
    Optional<Subject> findBySubjectCodeAndSchedule(String subjectCode, String schedule);
}