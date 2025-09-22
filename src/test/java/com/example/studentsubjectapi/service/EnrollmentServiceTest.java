package com.example.studentsubjectapi.service;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.repository.EnrollmentRepository;
import com.example.studentsubjectapi.repository.StudentRepository;
import com.example.studentsubjectapi.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Student student1;
    private Subject subject1;
    private Enrollment enrollment1;

    @BeforeEach
    void setUp() {
        student1 = new Student("João Silva", "2023001");
        student1.setId(1L);
        
        subject1 = new Subject("MAT001", "Matemática Básica", "A");
        subject1.setId(1L);
        
        enrollment1 = new Enrollment(student1, subject1);
        enrollment1.setId(1L);
    }

    @Test
    void enrollStudentInSubject_WithValidData_ShouldReturnEnrollment() {
        // Given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCode("MAT001")).thenReturn(subject1);
        when(enrollmentRepository.existsByStudentAndSubject(student1, subject1)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment1);

        // When
        Enrollment result = enrollmentService.enrollStudentInSubject("2023001", "MAT001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStudent()).isEqualTo(student1);
        assertThat(result.getSubject()).isEqualTo(subject1);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enrollStudentInSubject_WhenStudentNotFound_ShouldThrowException() {
        // Given
        when(studentRepository.findByRegistrationNumber("9999999")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.enrollStudentInSubject("9999999", "MAT001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estudante não encontrado com matrícula: 9999999");
    }

    @Test
    void enrollStudentInSubject_WhenSubjectNotFound_ShouldThrowException() {
        // Given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCode("INV001")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.enrollStudentInSubject("2023001", "INV001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Disciplina não encontrada com código: INV001");
    }

    @Test
    void enrollStudentInSubject_WhenAlreadyEnrolled_ShouldThrowException() {
        // Given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCode("MAT001")).thenReturn(subject1);
        when(enrollmentRepository.existsByStudentAndSubject(student1, subject1)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.enrollStudentInSubject("2023001", "MAT001"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Estudante já está matriculado nesta disciplina");
    }

    @Test
    void getEnrollmentsByStudent_WithValidStudent_ShouldReturnEnrollments() {
        // Given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(enrollmentRepository.findByStudent(student1)).thenReturn(Arrays.asList(enrollment1));

        // When
        List<Enrollment> result = enrollmentService.getEnrollmentsByStudent("2023001");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(enrollment1);
        verify(enrollmentRepository, times(1)).findByStudent(student1);
    }

    @Test
    void getEnrollmentsByStudent_WhenStudentNotFound_ShouldThrowException() {
        // Given
        when(studentRepository.findByRegistrationNumber("9999999")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.getEnrollmentsByStudent("9999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estudante não encontrado com matrícula: 9999999");
    }

    @Test
    void getEnrollmentsBySubject_WithValidSubject_ShouldReturnEnrollments() {
        // Given
        when(subjectRepository.findBySubjectCode("MAT001")).thenReturn(subject1);
        when(enrollmentRepository.findBySubject(subject1)).thenReturn(Arrays.asList(enrollment1));

        // When
        List<Enrollment> result = enrollmentService.getEnrollmentsBySubject("MAT001");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(enrollment1);
        verify(enrollmentRepository, times(1)).findBySubject(subject1);
    }

    @Test
    void getEnrollmentsBySubject_WhenSubjectNotFound_ShouldThrowException() {
        // Given
        when(subjectRepository.findBySubjectCode("INV001")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.getEnrollmentsBySubject("INV001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Disciplina não encontrada com código: INV001");
    }

    @Test
    void getAllEnrollments_ShouldReturnAllEnrollments() {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment1);
        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // When
        List<Enrollment> result = enrollmentService.getAllEnrollments();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(enrollment1);
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void unenrollStudentFromSubject_WithValidData_ShouldDeleteEnrollment() {
        // Given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCode("MAT001")).thenReturn(subject1);
        when(enrollmentRepository.findByStudentAndSubject(student1, subject1)).thenReturn(Optional.of(enrollment1));

        // When
        enrollmentService.unenrollStudentFromSubject("2023001", "MAT001");

        // Then
        verify(enrollmentRepository, times(1)).delete(enrollment1);
    }

    @Test
    void unenrollStudentFromSubject_WhenStudentNotFound_ShouldThrowException() {
        // Given
        when(studentRepository.findByRegistrationNumber("9999999")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.unenrollStudentFromSubject("9999999", "MAT001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estudante não encontrado com matrícula: 9999999");
    }

    @Test
    void unenrollStudentFromSubject_WhenNotEnrolled_ShouldThrowException() {
        // Given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCode("MAT001")).thenReturn(subject1);
        when(enrollmentRepository.findByStudentAndSubject(student1, subject1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> enrollmentService.unenrollStudentFromSubject("2023001", "MAT001"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Estudante não está matriculado nesta disciplina");
    }
}
