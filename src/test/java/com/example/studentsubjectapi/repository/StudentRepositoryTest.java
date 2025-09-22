package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private Student student1;
    private Student student2;
    private Student student3;

    @BeforeEach
    void setUp() {
        student1 = new Student("João Silva", "2023001");
        student2 = new Student("Maria Santos", "2023002");
        student3 = new Student("João Oliveira", "2023003");
    }

    @Test
    void findByRegistrationNumber_WhenStudentExists_ShouldReturnStudent() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        Student found = studentRepository.findByRegistrationNumber("2023001");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("João Silva");
        assertThat(found.getRegistrationNumber()).isEqualTo("2023001");
    }

    @Test
    void findByRegistrationNumber_WhenStudentDoesNotExist_ShouldReturnNull() {
        // Given
        entityManager.persistAndFlush(student1);

        // When
        Student found = studentRepository.findByRegistrationNumber("9999999");

        // Then
        assertThat(found).isNull();
    }

    @Test
    void findByNameContaining_WhenExactMatch_ShouldReturnStudent() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        List<Student> found = studentRepository.findByNameContaining("João Silva");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("João Silva");
    }

    @Test
    void findByNameContaining_WhenPartialMatch_ShouldReturnMatchingStudents() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(student3);

        // When
        List<Student> found = studentRepository.findByNameContaining("João");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Student::getName)
                .containsExactlyInAnyOrder("João Silva", "João Oliveira");
    }

    @Test
    void findByNameContaining_WhenCaseInsensitive_ShouldReturnMatchingStudents() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        List<Student> found = studentRepository.findByNameContaining("joão");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("João Silva");
    }

    @Test
    void findByNameContaining_WhenNoMatch_ShouldReturnEmptyList() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        List<Student> found = studentRepository.findByNameContaining("Pedro");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByNameContaining_WhenEmptyString_ShouldReturnAllStudents() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(student3);

        // When
        List<Student> found = studentRepository.findByNameContaining("");

        // Then
        assertThat(found).hasSize(3);
    }

    @Test
    void save_ShouldPersistStudentWithGeneratedId() {
        // Given
        Student newStudent = new Student("Pedro Costa", "2023004");

        // When
        Student saved = studentRepository.save(newStudent);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Pedro Costa");
        assertThat(saved.getRegistrationNumber()).isEqualTo("2023004");
    }

    @Test
    void findAll_ShouldReturnAllStudents() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(student3);

        // When
        List<Student> allStudents = studentRepository.findAll();

        // Then
        assertThat(allStudents).hasSize(3);
        assertThat(allStudents).extracting(Student::getName)
                .containsExactlyInAnyOrder("João Silva", "Maria Santos", "João Oliveira");
    }

    @Test
    void findByRegistrationNumber_WithUniqueConstraint_ShouldWorkCorrectly() {
        // Given
        Student student1 = new Student("João Silva", "2023001");
        Student student2 = new Student("Maria Santos", "2023002");
        
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        Student found1 = studentRepository.findByRegistrationNumber("2023001");
        Student found2 = studentRepository.findByRegistrationNumber("2023002");

        // Then
        assertThat(found1).isNotNull();
        assertThat(found1.getName()).isEqualTo("João Silva");
        
        assertThat(found2).isNotNull();
        assertThat(found2.getName()).isEqualTo("Maria Santos");
        
        assertThat(found1.getId()).isNotEqualTo(found2.getId());
    }
}
