package com.example.studentsubjectapi.controller;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void enrollStudent_WithValidData_ShouldReturnEnrollment() throws Exception {
        // Given
        when(enrollmentService.enrollStudentInSubject("2023001", "MAT001")).thenReturn(enrollment1);

        // When & Then
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.student.name").value("João Silva"))
                .andExpect(jsonPath("$.subject.subjectCode").value("MAT001"));
    }

    @Test
    void enrollStudent_WhenStudentNotFound_ShouldReturnBadRequest() throws Exception {
        // Given
        when(enrollmentService.enrollStudentInSubject("9999999", "MAT001"))
                .thenThrow(new IllegalArgumentException("Estudante não encontrado"));

        // When & Then
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "9999999")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enrollStudent_WhenAlreadyEnrolled_ShouldReturnConflict() throws Exception {
        // Given
        when(enrollmentService.enrollStudentInSubject("2023001", "MAT001"))
                .thenThrow(new IllegalStateException("Estudante já está matriculado"));

        // When & Then
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isConflict());
    }

    @Test
    void getEnrollmentsByStudent_WithValidStudent_ShouldReturnEnrollments() throws Exception {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment1);
        when(enrollmentService.getEnrollmentsByStudent("2023001")).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.name").value("João Silva"));
    }

    @Test
    void getEnrollmentsByStudent_WhenStudentNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(enrollmentService.getEnrollmentsByStudent("9999999"))
                .thenThrow(new IllegalArgumentException("Estudante não encontrado"));

        // When & Then
        mockMvc.perform(get("/enrollments/student/9999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEnrollmentsBySubject_WithValidSubject_ShouldReturnEnrollments() throws Exception {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment1);
        when(enrollmentService.getEnrollmentsBySubject("MAT001")).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/enrollments/subject/MAT001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].subject.subjectCode").value("MAT001"));
    }

    @Test
    void getEnrollmentsBySubject_WhenSubjectNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(enrollmentService.getEnrollmentsBySubject("INV001"))
                .thenThrow(new IllegalArgumentException("Disciplina não encontrada"));

        // When & Then
        mockMvc.perform(get("/enrollments/subject/INV001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllEnrollments_ShouldReturnAllEnrollments() throws Exception {
        // Given
        List<Enrollment> enrollments = Arrays.asList(enrollment1);
        when(enrollmentService.getAllEnrollments()).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void unenrollStudent_WithValidData_ShouldReturnOk() throws Exception {
        // Given
        // No exception thrown means success - mock the service to not throw any exception

        // When & Then
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());
    }

    @Test
    void unenrollStudent_WhenStudentNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Estudante não encontrado"))
                .when(enrollmentService).unenrollStudentFromSubject("9999999", "MAT001");

        // When & Then
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "9999999")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unenrollStudent_WhenNotEnrolled_ShouldReturnBadRequest() throws Exception {
        // Given
        doThrow(new IllegalStateException("Estudante não está matriculado"))
                .when(enrollmentService).unenrollStudentFromSubject("2023001", "MAT001");

        // When & Then
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isBadRequest());
    }
}
