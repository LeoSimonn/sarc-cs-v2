package com.example.studentsubjectapi.service;

import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;
    private Student student3;

    @BeforeEach
    void setUp() {
        student1 = new Student("João Silva", "2023001");
        student1.setId(1L);
        
        student2 = new Student("Maria Santos", "2023002");
        student2.setId(2L);
        
        student3 = new Student("João Oliveira", "2023003");
        student3.setId(3L);
    }

    @Test
    void addStudent_ShouldReturnSavedStudent() {
        // Given
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // When
        Student result = studentService.addStudent(student1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Silva");
        assertThat(result.getRegistrationNumber()).isEqualTo("2023001");
        verify(studentRepository, times(1)).save(student1);
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents() {
        // Given
        List<Student> students = Arrays.asList(student1, student2, student3);
        when(studentRepository.findAll()).thenReturn(students);

        // When
        List<Student> result = studentService.getAllStudents();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(student1, student2, student3);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentByRegistrationNumber_WhenStudentExists_ShouldReturnStudent() {
        // Given
        String registrationNumber = "2023001";
        when(studentRepository.findByRegistrationNumber(registrationNumber)).thenReturn(student1);

        // When
        Optional<Student> result = studentService.getStudentByRegistrationNumber(registrationNumber);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("João Silva");
        assertThat(result.get().getRegistrationNumber()).isEqualTo("2023001");
        verify(studentRepository, times(1)).findByRegistrationNumber(registrationNumber);
    }

    @Test
    void getStudentByRegistrationNumber_WhenStudentDoesNotExist_ShouldReturnEmpty() {
        // Given
        String registrationNumber = "9999999";
        when(studentRepository.findByRegistrationNumber(registrationNumber)).thenReturn(null);

        // When
        Optional<Student> result = studentService.getStudentByRegistrationNumber(registrationNumber);

        // Then
        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findByRegistrationNumber(registrationNumber);
    }

    @Test
    void getStudentsByName_WhenNameExists_ShouldReturnMatchingStudents() {
        // Given
        String name = "João";
        List<Student> matchingStudents = Arrays.asList(student1, student3);
        when(studentRepository.findByNameContaining(name)).thenReturn(matchingStudents);

        // When
        List<Student> result = studentService.getStudentsByName(name);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(student1, student3);
        verify(studentRepository, times(1)).findByNameContaining(name);
    }

    @Test
    void getStudentsByName_WhenNoMatch_ShouldReturnEmptyList() {
        // Given
        String name = "Pedro";
        when(studentRepository.findByNameContaining(name)).thenReturn(Arrays.asList());

        // When
        List<Student> result = studentService.getStudentsByName(name);

        // Then
        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findByNameContaining(name);
    }

    @Test
    void getStudentsByName_WhenPartialMatch_ShouldReturnMatchingStudents() {
        // Given
        String partialName = "Silva";
        List<Student> matchingStudents = Arrays.asList(student1);
        when(studentRepository.findByNameContaining(partialName)).thenReturn(matchingStudents);

        // When
        List<Student> result = studentService.getStudentsByName(partialName);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("João Silva");
        verify(studentRepository, times(1)).findByNameContaining(partialName);
    }
}
