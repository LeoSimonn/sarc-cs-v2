package com.example.studentsubjectapi.service;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.repository.EnrollmentRepository;
import com.example.studentsubjectapi.repository.StudentRepository;
import com.example.studentsubjectapi.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                           StudentRepository studentRepository,
                           SubjectRepository subjectRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    public Enrollment enrollStudentInSubject(String studentRegistrationNumber, String subjectCode, String schedule) {
        // buscar estudante por matricula
        Student student = studentRepository.findByRegistrationNumber(studentRegistrationNumber);
        if (student == null) {
            throw new IllegalArgumentException("Estudante nao encontrado com matricula: " + studentRegistrationNumber);
        }

        // buscar disciplina pelo codigo e horario
        Subject subject = subjectRepository.findBySubjectCodeAndSchedule(subjectCode, schedule)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada com codigo: " + subjectCode + " e horario: " + schedule));

        // verificar se ja existe matricula
        if (enrollmentRepository.existsByStudentAndSubject(student, subject)) {
            throw new IllegalStateException("Estudante ja esta matriculado nesta disciplina e horario");
        }

        // criar nova matricula
        Enrollment enrollment = new Enrollment(student, subject);
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentRegistrationNumber) {
        Student student = studentRepository.findByRegistrationNumber(studentRegistrationNumber);
        if (student == null) {
            throw new IllegalArgumentException("Estudante nao encontrado com matricula: " + studentRegistrationNumber);
        }
        return enrollmentRepository.findByStudent(student);
    }

    public List<Enrollment> getEnrollmentsBySubject(String subjectCode) {
        Subject subject = subjectRepository.findBySubjectCode(subjectCode);
        if (subject == null) {
            throw new IllegalArgumentException("Disciplina nao encontrada com codigo: " + subjectCode);
        }
        return enrollmentRepository.findBySubject(subject);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public void unenrollStudentFromSubject(String studentRegistrationNumber, String subjectCode, String schedule) {
        Student student = studentRepository.findByRegistrationNumber(studentRegistrationNumber);
        if (student == null) {
            throw new IllegalArgumentException("Estudante nao encontrado com matricula: " + studentRegistrationNumber);
        }

        Subject subject = subjectRepository.findBySubjectCodeAndSchedule(subjectCode, schedule)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada com codigo: " + subjectCode + " e horario: " + schedule));

        Optional<Enrollment> enrollment = enrollmentRepository.findByStudentAndSubject(student, subject);
        if (enrollment.isPresent()) {
            enrollmentRepository.delete(enrollment.get());
        } else {
            throw new IllegalStateException("Estudante nao esta matriculado nesta disciplina e horario");
        }
    }
}