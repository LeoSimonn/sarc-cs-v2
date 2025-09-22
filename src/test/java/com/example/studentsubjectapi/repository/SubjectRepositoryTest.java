package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SubjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubjectRepository subjectRepository;

    private Subject subject1;
    private Subject subject2;
    private Subject subject3;

    @BeforeEach
    void setUp() {
        subject1 = new Subject("MAT001", "Matemática Básica", "A");
        subject2 = new Subject("FIS001", "Física I", "B");
        subject3 = new Subject("MAT001", "Matemática Básica", "C");
    }

    @Test
    void findBySubjectCode_WhenSubjectExists_ShouldReturnSubject() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);

        // When
        Subject found = subjectRepository.findBySubjectCode("MAT001");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getSubjectCode()).isEqualTo("MAT001");
        assertThat(found.getSubjectName()).isEqualTo("Matemática Básica");
        assertThat(found.getSchedule()).isEqualTo("A");
    }

    @Test
    void findBySubjectCode_WhenSubjectDoesNotExist_ShouldReturnNull() {
        // Given
        entityManager.persistAndFlush(subject1);

        // When
        Subject found = subjectRepository.findBySubjectCode("INV001");

        // Then
        assertThat(found).isNull();
    }

    @Test
    void findBySubjectNameContaining_WhenExactMatch_ShouldReturnSubject() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);

        // When
        List<Subject> found = subjectRepository.findBySubjectNameContaining("Matemática Básica");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getSubjectName()).isEqualTo("Matemática Básica");
    }

    @Test
    void findBySubjectNameContaining_WhenPartialMatch_ShouldReturnMatchingSubjects() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);
        entityManager.persistAndFlush(subject3);

        // When
        List<Subject> found = subjectRepository.findBySubjectNameContaining("Matemática");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Subject::getSubjectName)
                .containsExactlyInAnyOrder("Matemática Básica", "Matemática Básica");
    }

    @Test
    void findBySubjectNameContaining_WhenCaseInsensitive_ShouldReturnMatchingSubjects() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);

        // When
        List<Subject> found = subjectRepository.findBySubjectNameContaining("matemática");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getSubjectName()).isEqualTo("Matemática Básica");
    }

    @Test
    void findBySubjectNameContaining_WhenNoMatch_ShouldReturnEmptyList() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);

        // When
        List<Subject> found = subjectRepository.findBySubjectNameContaining("Química");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistSubjectWithGeneratedId() {
        // Given
        Subject newSubject = new Subject("QUI001", "Química I", "D");

        // When
        Subject saved = subjectRepository.save(newSubject);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSubjectCode()).isEqualTo("QUI001");
        assertThat(saved.getSubjectName()).isEqualTo("Química I");
        assertThat(saved.getSchedule()).isEqualTo("D");
    }

    @Test
    void findAll_ShouldReturnAllSubjects() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);
        entityManager.persistAndFlush(subject3);

        // When
        List<Subject> allSubjects = subjectRepository.findAll();

        // Then
        assertThat(allSubjects).hasSize(3);
        assertThat(allSubjects).extracting(Subject::getSubjectName)
                .containsExactlyInAnyOrder("Matemática Básica", "Física I", "Matemática Básica");
    }

    @Test
    void save_WithSameCodeAndNameDifferentSchedule_ShouldPersistBothSubjects() {
        // Given
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");

        // When
        Subject savedA = subjectRepository.save(matA);
        Subject savedB = subjectRepository.save(matB);
        entityManager.flush();

        // Then
        assertThat(savedA.getId()).isNotNull();
        assertThat(savedB.getId()).isNotNull();
        assertThat(savedA.getId()).isNotEqualTo(savedB.getId());
        
        assertThat(savedA.getSubjectCode()).isEqualTo(savedB.getSubjectCode());
        assertThat(savedA.getSubjectName()).isEqualTo(savedB.getSubjectName());
        assertThat(savedA.getSchedule()).isEqualTo("A");
        assertThat(savedB.getSchedule()).isEqualTo("B");
    }

    @Test
    void findBySubjectCode_WithMultipleSubjectsSameCode_ShouldReturnFirstFound() {
        // Given
        entityManager.persistAndFlush(subject1); // MAT001 - A
        entityManager.persistAndFlush(subject3); // MAT001 - C

        // When
        Subject found = subjectRepository.findBySubjectCode("MAT001");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getSubjectCode()).isEqualTo("MAT001");
        // Pode retornar qualquer um dos dois, dependendo da implementação do JPA
        assertThat(found.getSchedule()).isIn("A", "C");
    }

    @Test
    void findBySubjectNameContaining_WithEmptyString_ShouldReturnAllSubjects() {
        // Given
        entityManager.persistAndFlush(subject1);
        entityManager.persistAndFlush(subject2);
        entityManager.persistAndFlush(subject3);

        // When
        List<Subject> found = subjectRepository.findBySubjectNameContaining("");

        // Then
        assertThat(found).hasSize(3);
    }

    @Test
    void save_WithValidScheduleCodes_ShouldPersistCorrectly() {
        // Given
        Subject[] subjects = {
            new Subject("MAT001", "Matemática", "A"),
            new Subject("FIS001", "Física", "B"),
            new Subject("QUI001", "Química", "C"),
            new Subject("BIO001", "Biologia", "D"),
            new Subject("HIS001", "História", "E"),
            new Subject("GEO001", "Geografia", "F"),
            new Subject("ART001", "Artes", "G")
        };

        // When
        for (Subject subject : subjects) {
            subjectRepository.save(subject);
        }
        entityManager.flush();

        // Then
        List<Subject> allSubjects = subjectRepository.findAll();
        assertThat(allSubjects).hasSize(7);
        
        String[] expectedSchedules = {"A", "B", "C", "D", "E", "F", "G"};
        assertThat(allSubjects).extracting(Subject::getSchedule)
                .containsExactlyInAnyOrder(expectedSchedules);
    }
}
