package com.example.studentsubjectapi.controller;

import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.service.SubjectService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubjectController.class)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubjectService subjectService;

    @Autowired
    private ObjectMapper objectMapper;

    private Subject subject1;
    private Subject subject2;
    private Subject subject3;

    @BeforeEach
    void setUp() {
        subject1 = new Subject("MAT001", "Matematica Basica", "A");
        subject1.setId(1L);
        
        subject2 = new Subject("FIS001", "Fisica I", "B");
        subject2.setId(2L);
        
        subject3 = new Subject("MAT001", "Matematica Basica", "C");
        subject3.setId(3L);
    }

    @Test
    void registerSubject_WithValidSubject_ShouldReturnCreatedSubject() throws Exception {
        // Given
        Subject newSubject = new Subject("QUI001", "Quimica I", "D");
        when(subjectService.addSubject(any(Subject.class))).thenReturn(newSubject);

        // When & Then
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSubject)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subjectCode").value("QUI001"))
                .andExpect(jsonPath("$.subjectName").value("Quimica I"))
                .andExpect(jsonPath("$.schedule").value("D"));
    }

    @Test
    void registerSubject_WithInvalidSubject_ShouldReturnBadRequest() throws Exception {
        // Given
        Subject invalidSubject = new Subject("", "", ""); // Código, nome e horário vazios

        // When & Then
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSubject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSubjectByCode_WhenSubjectExists_ShouldReturnSubject() throws Exception {
        // Given
        String subjectCode = "MAT001";
        when(subjectService.getSubjectByCode(subjectCode)).thenReturn(subject1);

        // When & Then
        mockMvc.perform(get("/subjects/{subjectCode}", subjectCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subjectCode").value("MAT001"))
                .andExpect(jsonPath("$.subjectName").value("Matematica Basica"))
                .andExpect(jsonPath("$.schedule").value("A"));
    }

    @Test
    void getSubjectByCode_WhenSubjectDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        String subjectCode = "INV001";
        when(subjectService.getSubjectByCode(subjectCode)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/subjects/{subjectCode}", subjectCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllSubjects_ShouldReturnAllSubjects() throws Exception {
        // Given
        List<Subject> subjects = Arrays.asList(subject1, subject2, subject3);
        when(subjectService.getAllSubjects()).thenReturn(subjects);

        // When & Then
        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].subjectCode").value("MAT001"))
                .andExpect(jsonPath("$[0].subjectName").value("Matemática Básica"))
                .andExpect(jsonPath("$[0].schedule").value("A"))
                .andExpect(jsonPath("$[1].subjectCode").value("FIS001"))
                .andExpect(jsonPath("$[1].subjectName").value("Física I"))
                .andExpect(jsonPath("$[1].schedule").value("B"));
    }

    @Test
    void enrollStudentInSubject_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String subjectCode = "MAT001";
        String studentRegistrationNumber = "2023001";

        // When & Then
        mockMvc.perform(post("/subjects/{subjectCode}/enroll", subjectCode)
                .param("studentRegistrationNumber", studentRegistrationNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("Student enrolled successfully"));
    }

    @Test
    void registerSubject_WithSameCodeAndNameDifferentSchedule_ShouldReturnCreatedSubject() throws Exception {
        // Given
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");
        
        when(subjectService.addSubject(any(Subject.class)))
            .thenReturn(matA)
            .thenReturn(matB);

        // When & Then - Primeira disciplina
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule").value("A"));

        // When & Then - Segunda disciplina (mesmo código e nome, horário diferente)
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule").value("B"));
    }

    @Test
    void getAllSubjects_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
