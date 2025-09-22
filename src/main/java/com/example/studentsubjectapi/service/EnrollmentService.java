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

    public Enrollment enrollStudentInSubject(String studentRegistrationNumber, String subjectCode) {
        // Buscar estudante por matrícula
        Student student = studentRepository.findByRegistrationNumber(studentRegistrationNumber);
        if (student == null) {
            throw new IllegalArgumentException("Estudante não encontrado com matrícula: " + studentRegistrationNumber);
        }

        // Buscar disciplina por código
        Subject subject = subjectRepository.findBySubjectCode(subjectCode);
        if (subject == null) {
            throw new IllegalArgumentException("Disciplina não encontrada com código: " + subjectCode);
        }

        // Verificar se já existe matrícula
        if (enrollmentRepository.existsByStudentAndSubject(student, subject)) {
            throw new IllegalStateException("Estudante já está matriculado nesta disciplina");
        }

        // Criar nova matrícula
        Enrollment enrollment = new Enrollment(student, subject);
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentRegistrationNumber) {
        Student student = studentRepository.findByRegistrationNumber(studentRegistrationNumber);
        if (student == null) {
            throw new IllegalArgumentException("Estudante não encontrado com matrícula: " + studentRegistrationNumber);
        }
        return enrollmentRepository.findByStudent(student);
    }

    public List<Enrollment> getEnrollmentsBySubject(String subjectCode) {
        Subject subject = subjectRepository.findBySubjectCode(subjectCode);
        if (subject == null) {
            throw new IllegalArgumentException("Disciplina não encontrada com código: " + subjectCode);
        }
        return enrollmentRepository.findBySubject(subject);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public void unenrollStudentFromSubject(String studentRegistrationNumber, String subjectCode) {
        Student student = studentRepository.findByRegistrationNumber(studentRegistrationNumber);
        if (student == null) {
            throw new IllegalArgumentException("Estudante não encontrado com matrícula: " + studentRegistrationNumber);
        }

        Subject subject = subjectRepository.findBySubjectCode(subjectCode);
        if (subject == null) {
            throw new IllegalArgumentException("Disciplina não encontrada com código: " + subjectCode);
        }

        Optional<Enrollment> enrollment = enrollmentRepository.findByStudentAndSubject(student, subject);
        if (enrollment.isPresent()) {
            enrollmentRepository.delete(enrollment.get());
        } else {
            throw new IllegalStateException("Estudante não está matriculado nesta disciplina");
        }
    }
}
