package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    Optional<Enrollment> findByStudentAndSubject(Student student, Subject subject);
    
    List<Enrollment> findByStudent(Student student);
    
    List<Enrollment> findBySubject(Subject subject);
    
    boolean existsByStudentAndSubject(Student student, Subject subject);
}
