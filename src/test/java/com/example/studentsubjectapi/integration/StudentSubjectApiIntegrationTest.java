package com.example.studentsubjectapi.integration;

import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.repository.StudentRepository;
import com.example.studentsubjectapi.repository.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class StudentSubjectApiIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        studentRepository.deleteAll();
        subjectRepository.deleteAll();
    }

    @Test
    void studentWorkflow_ShouldWorkEndToEnd() throws Exception {
        // 1. Cadastrar estudante
        Student newStudent = new Student("João Silva", "2023001");
        
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.registrationNumber").value("2023001"))
                .andExpect(jsonPath("$.id").exists());

        // 2. Buscar estudante por matrícula
        mockMvc.perform(get("/students/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.registrationNumber").value("2023001"));

        // 3. Buscar estudante por nome (parcial)
        mockMvc.perform(get("/students/search")
                .param("name", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("João Silva"));

        // 4. Listar todos os estudantes
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void subjectWorkflow_ShouldWorkEndToEnd() throws Exception {
        // 1. Cadastrar disciplina
        Subject newSubject = new Subject("MAT001", "Matemática Básica", "A");
        
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSubject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectCode").value("MAT001"))
                .andExpect(jsonPath("$.subjectName").value("Matemática Básica"))
                .andExpect(jsonPath("$.schedule").value("A"))
                .andExpect(jsonPath("$.id").exists());

        // 2. Buscar disciplina por código
        mockMvc.perform(get("/subjects/MAT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectCode").value("MAT001"))
                .andExpect(jsonPath("$.subjectName").value("Matemática Básica"))
                .andExpect(jsonPath("$.schedule").value("A"));

        // 3. Listar todas as disciplinas
        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void multipleSubjectsWithSameCode_ShouldWorkCorrectly() throws Exception {
        // 1. Cadastrar primeira disciplina
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule").value("A"));

        // 2. Cadastrar segunda disciplina (mesmo código e nome, horário diferente)
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");
        
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedule").value("B"));

        // 3. Verificar que ambas foram salvas
        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].subjectCode").value(org.hamcrest.Matchers.hasItems("MAT001", "MAT001")))
                .andExpect(jsonPath("$[*].schedule").value(org.hamcrest.Matchers.hasItems("A", "B")));
    }

    @Test
    void studentSearchWithMultipleMatches_ShouldReturnAllMatches() throws Exception {
        // 1. Cadastrar múltiplos estudantes com nomes similares
        Student joao1 = new Student("João Silva", "2023001");
        Student joao2 = new Student("João Oliveira", "2023002");
        Student maria = new Student("Maria Santos", "2023003");

        studentRepository.save(joao1);
        studentRepository.save(joao2);
        studentRepository.save(maria);

        // 2. Buscar por "João" - deve retornar 2 estudantes
        mockMvc.perform(get("/students/search")
                .param("name", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name").value(org.hamcrest.Matchers.hasItems("João Silva", "João Oliveira")));

        // 3. Buscar por "Maria" - deve retornar 1 estudante
        mockMvc.perform(get("/students/search")
                .param("name", "Maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Maria Santos"));
    }

    @Test
    void invalidRequests_ShouldReturnAppropriateErrors() throws Exception {
        // 1. Estudante com dados inválidos
        Student invalidStudent = new Student("", "");
        
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest());

        // 2. Buscar estudante inexistente
        mockMvc.perform(get("/students/9999999"))
                .andExpect(status().isNotFound());

        // 3. Buscar disciplina inexistente
        mockMvc.perform(get("/subjects/INV001"))
                .andExpect(status().isNotFound());

        // 4. Disciplina com dados inválidos
        Subject invalidSubject = new Subject("", "", "");
        
        mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSubject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeWorkflow_StudentsAndSubjects_ShouldWorkTogether() throws Exception {
        // 1. Cadastrar estudantes
        Student student1 = new Student("Ana Silva", "2023001");
        Student student2 = new Student("Carlos Santos", "2023002");
        
        studentRepository.save(student1);
        studentRepository.save(student2);

        // 2. Cadastrar disciplinas
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");
        Subject fis = new Subject("FIS001", "Física I", "C");
        
        subjectRepository.save(matA);
        subjectRepository.save(matB);
        subjectRepository.save(fis);

        // 3. Verificar estudantes cadastrados
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 4. Verificar disciplinas cadastradas
        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // 5. Buscar estudante específico
        mockMvc.perform(get("/students/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Silva"));

        // 6. Buscar disciplina específica
        mockMvc.perform(get("/subjects/MAT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectCode").value("MAT001"));

        // 7. Buscar estudantes por nome
        mockMvc.perform(get("/students/search")
                .param("name", "Silva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Ana Silva"));
    }
}
