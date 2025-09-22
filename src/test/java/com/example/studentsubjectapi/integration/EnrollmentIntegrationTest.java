package com.example.studentsubjectapi.integration;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.repository.EnrollmentRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class EnrollmentIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        subjectRepository.deleteAll();
    }

    @Test
    void enrollmentWorkflow_ShouldWorkEndToEnd() throws Exception {
        // 1. Cadastrar estudante
        Student student = new Student("João Silva", "2023001");
        studentRepository.save(student);

        // 2. Cadastrar disciplina
        Subject subject = new Subject("MAT001", "Matemática Básica", "A");
        subjectRepository.save(subject);

        // 3. Matricular estudante na disciplina
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.student.name").value("João Silva"))
                .andExpect(jsonPath("$.subject.subjectCode").value("MAT001"));

        // 4. Verificar matrículas do estudante
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.name").value("João Silva"))
                .andExpect(jsonPath("$[0].subject.subjectCode").value("MAT001"));

        // 5. Verificar matrículas da disciplina
        mockMvc.perform(get("/enrollments/subject/MAT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.name").value("João Silva"));

        // 6. Desmatricular estudante
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 7. Verificar que não há mais matrículas
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void multipleEnrollments_ShouldWorkCorrectly() throws Exception {
        // 1. Cadastrar estudantes
        Student student1 = new Student("João Silva", "2023001");
        Student student2 = new Student("Maria Santos", "2023002");
        studentRepository.save(student1);
        studentRepository.save(student2);

        // 2. Cadastrar disciplinas
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");
        Subject fis = new Subject("FIS001", "Física I", "C");
        subjectRepository.save(matA);
        subjectRepository.save(matB);
        subjectRepository.save(fis);

        // 3. Matricular João em Matemática A
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 4. Matricular Maria em Matemática A
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023002")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 5. Matricular João em Física
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "FIS001"))
                .andExpect(status().isOk());

        // 6. Verificar todas as matrículas
        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        // 7. Verificar matrículas de João
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // 8. Verificar matrículas de Maria
        mockMvc.perform(get("/enrollments/student/2023002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void duplicateEnrollment_ShouldReturnConflict() throws Exception {
        // 1. Cadastrar estudante e disciplina
        Student student = new Student("João Silva", "2023001");
        Subject subject = new Subject("MAT001", "Matemática Básica", "A");
        studentRepository.save(student);
        subjectRepository.save(subject);

        // 2. Primeira matrícula - deve funcionar
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 3. Segunda matrícula - deve retornar conflito
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isConflict());
    }

    @Test
    void enrollmentWithInvalidData_ShouldReturnAppropriateErrors() throws Exception {
        // 1. Tentar matricular estudante inexistente
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "9999999")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isBadRequest());

        // 2. Tentar matricular em disciplina inexistente
        Student student = new Student("João Silva", "2023001");
        studentRepository.save(student);

        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "INV001"))
                .andExpect(status().isBadRequest());

        // 3. Tentar desmatricular estudante inexistente
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "9999999")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isNotFound());

        // 4. Tentar desmatricular de disciplina inexistente
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "INV001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void enrollmentWithSameSubjectDifferentSchedules_ShouldWorkCorrectly() throws Exception {
        // 1. Cadastrar estudante
        Student student = new Student("João Silva", "2023001");
        studentRepository.save(student);

        // 2. Cadastrar mesma disciplina em horários diferentes
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");
        subjectRepository.save(matA);
        subjectRepository.save(matB);

        // 3. Matricular no horário A
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 4. Verificar que só há uma matrícula (primeira encontrada)
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void completeAcademicWorkflow_ShouldWorkEndToEnd() throws Exception {
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

        // 3. Matricular Ana em Matemática A
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 4. Matricular Carlos em Matemática B
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023002")
                .param("subjectCode", "MAT001"))
                .andExpect(status().isOk());

        // 5. Matricular Ana em Física
        mockMvc.perform(post("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "FIS001"))
                .andExpect(status().isOk());

        // 6. Verificar todas as matrículas
        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        // 7. Verificar matrículas de Ana
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // 8. Verificar matrículas de Carlos
        mockMvc.perform(get("/enrollments/student/2023002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        // 9. Desmatricular Ana de Física
        mockMvc.perform(delete("/enrollments")
                .param("studentRegistrationNumber", "2023001")
                .param("subjectCode", "FIS001"))
                .andExpect(status().isOk());

        // 10. Verificar que Ana agora tem apenas 1 matrícula
        mockMvc.perform(get("/enrollments/student/2023001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
