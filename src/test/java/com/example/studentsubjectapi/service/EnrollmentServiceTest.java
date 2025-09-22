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
        // given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCodeAndSchedule("MAT001", "A")).thenReturn(Optional.of(subject1));
        when(enrollmentRepository.existsByStudentAndSubject(student1, subject1)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment1);

        // when
        Enrollment result = enrollmentService.enrollStudentInSubject("2023001", "MAT001", "A");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStudent()).isEqualTo(student1);
        assertThat(result.getSubject()).isEqualTo(subject1);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enrollStudentInSubject_WhenStudentNotFound_ShouldThrowException() {
        // given
        when(studentRepository.findByRegistrationNumber("9999999")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> enrollmentService.enrollStudentInSubject("9999999", "MAT001", "A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estudante nao encontrado com matricula: 9999999");
    }

    @Test
    void enrollStudentInSubject_WhenSubjectNotFound_ShouldThrowException() {
        // given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCodeAndSchedule("INV001", "A")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> enrollmentService.enrollStudentInSubject("2023001", "INV001", "A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Disciplina nao encontrada com codigo: INV001 e horario: A");
    }

    @Test
    void enrollStudentInSubject_WhenAlreadyEnrolled_ShouldThrowException() {
        // given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCodeAndSchedule("MAT001", "A")).thenReturn(Optional.of(subject1));
        when(enrollmentRepository.existsByStudentAndSubject(student1, subject1)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> enrollmentService.enrollStudentInSubject("2023001", "MAT001", "A"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Estudante ja esta matriculado nesta disciplina e horario");
    }

    @Test
    void getEnrollmentsByStudent_WithValidStudent_ShouldReturnEnrollments() {
        // given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(enrollmentRepository.findByStudent(student1)).thenReturn(Arrays.asList(enrollment1));

        // when
        List<Enrollment> result = enrollmentService.getEnrollmentsByStudent("2023001");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(enrollment1);
        verify(enrollmentRepository, times(1)).findByStudent(student1);
    }

    @Test
    void getEnrollmentsByStudent_WhenStudentNotFound_ShouldThrowException() {
        // given
        when(studentRepository.findByRegistrationNumber("9999999")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> enrollmentService.getEnrollmentsByStudent("9999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estudante nao encontrado com matricula: 9999999");
    }

    @Test
    void getEnrollmentsBySubject_WithValidSubject_ShouldReturnEnrollments() {
        // given
        when(subjectRepository.findBySubjectCode("MAT001")).thenReturn(subject1);
        when(enrollmentRepository.findBySubject(subject1)).thenReturn(Arrays.asList(enrollment1));

        // when
        List<Enrollment> result = enrollmentService.getEnrollmentsBySubject("MAT001");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(enrollment1);
        verify(enrollmentRepository, times(1)).findBySubject(subject1);
    }

    @Test
    void getEnrollmentsBySubject_WhenSubjectNotFound_ShouldThrowException() {
        // given
        when(subjectRepository.findBySubjectCode("INV001")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> enrollmentService.getEnrollmentsBySubject("INV001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Disciplina nao encontrada com codigo: INV001");
    }

    @Test
    void getAllEnrollments_ShouldReturnAllEnrollments() {
        // given
        List<Enrollment> enrollments = Arrays.asList(enrollment1);
        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // when
        List<Enrollment> result = enrollmentService.getAllEnrollments();

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(enrollment1);
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void unenrollStudentFromSubject_WithValidData_ShouldDeleteEnrollment() {
        // given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCodeAndSchedule("MAT001", "A")).thenReturn(Optional.of(subject1));
        when(enrollmentRepository.findByStudentAndSubject(student1, subject1)).thenReturn(Optional.of(enrollment1));

        // when
        enrollmentService.unenrollStudentFromSubject("2023001", "MAT001", "A");

        // then
        verify(enrollmentRepository, times(1)).delete(enrollment1);
    }

    @Test
    void unenrollStudentFromSubject_WhenStudentNotFound_ShouldThrowException() {
        // given
        when(studentRepository.findByRegistrationNumber("9999999")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> enrollmentService.unenrollStudentFromSubject("9999999", "MAT001", "A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estudante nao encontrado com matricula: 9999999");
    }

    @Test
    void unenrollStudentFromSubject_WhenNotEnrolled_ShouldThrowException() {
        // given
        when(studentRepository.findByRegistrationNumber("2023001")).thenReturn(student1);
        when(subjectRepository.findBySubjectCodeAndSchedule("MAT001", "A")).thenReturn(Optional.of(subject1));
        when(enrollmentRepository.findByStudentAndSubject(student1, subject1)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> enrollmentService.unenrollStudentFromSubject("2023001", "MAT001", "A"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Estudante nao esta matriculado nesta disciplina e horario");
    }
}