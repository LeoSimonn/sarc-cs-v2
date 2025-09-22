package com.example.studentsubjectapi.controller;

import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.service.StudentService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student("João Silva", "2023001");
        student1.setId(1L);
        
        student2 = new Student("Maria Santos", "2023002");
        student2.setId(2L);
    }

    @Test
    void listAll_ShouldReturnAllStudents() throws Exception {
        // Given
        List<Student> students = Arrays.asList(student1, student2);
        when(studentService.getAllStudents()).thenReturn(students);

        // When & Then
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("João Silva"))
                .andExpect(jsonPath("$[0].registrationNumber").value("2023001"))
                .andExpect(jsonPath("$[1].name").value("Maria Santos"))
                .andExpect(jsonPath("$[1].registrationNumber").value("2023002"));
    }

    @Test
    void registerStudent_WithValidStudent_ShouldReturnCreatedStudent() throws Exception {
        // Given
        Student newStudent = new Student("Pedro Costa", "2023003");
        when(studentService.addStudent(any(Student.class))).thenReturn(newStudent);

        // When & Then
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Pedro Costa"))
                .andExpect(jsonPath("$.registrationNumber").value("2023003"));
    }

    @Test
    void registerStudent_WithInvalidStudent_ShouldReturnBadRequest() throws Exception {
        // Given
        Student invalidStudent = new Student("", ""); // Nome e matrícula vazios

        // When & Then
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStudentByRegistrationNumber_WhenStudentExists_ShouldReturnStudent() throws Exception {
        // Given
        String registrationNumber = "2023001";
        when(studentService.getStudentByRegistrationNumber(registrationNumber))
                .thenReturn(Optional.of(student1));

        // When & Then
        mockMvc.perform(get("/students/{registrationNumber}", registrationNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.registrationNumber").value("2023001"));
    }

    @Test
    void getStudentByRegistrationNumber_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        String registrationNumber = "9999999";
        when(studentService.getStudentByRegistrationNumber(registrationNumber))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/students/{registrationNumber}", registrationNumber))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStudentsByName_WhenStudentsExist_ShouldReturnMatchingStudents() throws Exception {
        // Given
        String name = "João";
        List<Student> matchingStudents = Arrays.asList(student1);
        when(studentService.getStudentsByName(name)).thenReturn(matchingStudents);

        // When & Then
        mockMvc.perform(get("/students/search")
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    void getStudentsByName_WhenNoStudentsMatch_ShouldReturnEmptyList() throws Exception {
        // Given
        String name = "Pedro";
        when(studentService.getStudentsByName(name)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/students/search")
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getStudentsByName_WithMultipleMatches_ShouldReturnAllMatchingStudents() throws Exception {
        // Given
        Student joao2 = new Student("João Oliveira", "2023003");
        joao2.setId(3L);
        
        String name = "João";
        List<Student> matchingStudents = Arrays.asList(student1, joao2);
        when(studentService.getStudentsByName(name)).thenReturn(matchingStudents);

        // When & Then
        mockMvc.perform(get("/students/search")
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("João Silva"))
                .andExpect(jsonPath("$[1].name").value("João Oliveira"));
    }
}
