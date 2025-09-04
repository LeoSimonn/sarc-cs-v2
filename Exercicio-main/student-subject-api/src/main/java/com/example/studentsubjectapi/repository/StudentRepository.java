package com.example.studentsubjectapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.studentsubjectapi.model.Student;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByRegistrationNumber(String registrationNumber);
    List<Student> findByNameContaining(String name);
}