package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Enrollment;
import com.example.studentsubjectapi.model.Student;
import com.example.studentsubjectapi.model.Subject;
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
class EnrollmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // @Autowired
    // private StudentRepository studentRepository;

    // @Autowired
    // private SubjectRepository subjectRepository;

    private Student student1;
    private Student student2;
    private Subject subject1;
    private Subject subject2;
    private Enrollment enrollment1;
    private Enrollment enrollment2;

    @BeforeEach
    void setUp() {
        student1 = new Student("João Silva", "2023001");
        student2 = new Student("Maria Santos", "2023002");
        
        subject1 = new Subject("MAT001", "Matemática Básica", "A");
        subject2 = new Subject("FIS001", "Física I", "B");
        
        enrollment1 = new Enrollment(student1, subject1);
        enrollment2 = new Enrollment(student2, subject1);
    }

    @Test
    void findByStudentAndSubject_WhenEnrollmentExists_ShouldReturnEnrollment() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(enrollment1);

        // When
        Optional<Enrollment> found = enrollmentRepository.findByStudentAndSubject(student1, subject1);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getStudent()).isEqualTo(student1);
        assertThat(found.get().getSubject()).isEqualTo(subject1);
    }

    @Test
    void findByStudentAndSubject_WhenEnrollmentDoesNotExist_ShouldReturnEmpty() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);

        // When
        Optional<Enrollment> found = enrollmentRepository.findByStudentAndSubject(student1, subject1);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByStudent_WhenStudentHasEnrollments_ShouldReturnEnrollments() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);
        entityManager.persistAndFlush(enrollment1);
        
        Enrollment enrollment2 = new Enrollment(student1, subject2);
        entityManager.persistAndFlush(enrollment2);

        // When
        List<Enrollment> found = enrollmentRepository.findByStudent(student1);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Enrollment::getStudent)
                .containsOnly(student1);
    }

    @Test
    void findByStudent_WhenStudentHasNoEnrollments_ShouldReturnEmptyList() {
        // Given
        entityManager.persistAndFlush(student1);

        // When
        List<Enrollment> found = enrollmentRepository.findByStudent(student1);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findBySubject_WhenSubjectHasEnrollments_ShouldReturnEnrollments() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(enrollment1);
        entityManager.persistAndFlush(enrollment2);

        // When
        List<Enrollment> found = enrollmentRepository.findBySubject(subject1);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Enrollment::getSubject)
                .containsOnly(subject1);
    }

    @Test
    void findBySubject_WhenSubjectHasNoEnrollments_ShouldReturnEmptyList() {
        // Given
        entityManager.persistAndFlush(subject1);

        // When
        List<Enrollment> found = enrollmentRepository.findBySubject(subject1);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByStudentAndSubject_WhenEnrollmentExists_ShouldReturnTrue() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(enrollment1);

        // When
        boolean exists = enrollmentRepository.existsByStudentAndSubject(student1, subject1);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByStudentAndSubject_WhenEnrollmentDoesNotExist_ShouldReturnFalse() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);

        // When
        boolean exists = enrollmentRepository.existsByStudentAndSubject(student1, subject1);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void save_ShouldPersistEnrollmentWithGeneratedId() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);
        Enrollment newEnrollment = new Enrollment(student1, subject1);

        // When
        Enrollment saved = enrollmentRepository.save(newEnrollment);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudent()).isEqualTo(student1);
        assertThat(saved.getSubject()).isEqualTo(subject1);
    }

    @Test
    void save_WithUniqueConstraint_ShouldPreventDuplicateEnrollments() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);
        
        Enrollment enrollment1 = new Enrollment(student1, subject1);
        entityManager.persistAndFlush(enrollment1);

        // When & Then
        // Tentar criar uma segunda matrícula para o mesmo estudante e disciplina
        Enrollment duplicateEnrollment = new Enrollment(student1, subject1);
        
        // O JPA deve lançar uma exceção devido à constraint única
        try {
            enrollmentRepository.save(duplicateEnrollment);
            entityManager.flush();
            // Se chegou aqui, a constraint não funcionou
            assertThat(false).as("Unique constraint should have prevented duplicate enrollment").isTrue();
        } catch (Exception e) {
            // Esperado - constraint única deve impedir duplicatas
            assertThat(e).isNotNull();
        }
    }

    @Test
    void findAll_ShouldReturnAllEnrollments() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);
        entityManager.persistAndFlush(enrollment1);
        entityManager.persistAndFlush(enrollment2);

        // When
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();

        // Then
        assertThat(allEnrollments).hasSize(2);
        assertThat(allEnrollments).extracting(Enrollment::getStudent)
                .containsExactlyInAnyOrder(student1, student2);
    }

    @Test
    void delete_ShouldRemoveEnrollment() {
        // Given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(enrollment1);

        // When
        enrollmentRepository.delete(enrollment1);
        entityManager.flush();

        // Then
        Optional<Enrollment> found = enrollmentRepository.findByStudentAndSubject(student1, subject1);
        assertThat(found).isEmpty();
    }
}
