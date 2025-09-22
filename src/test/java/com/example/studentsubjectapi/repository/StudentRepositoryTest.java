package com.example.studentsubjectapi.repository;

import com.example.studentsubjectapi.model.Student;
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
        // given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // when
        Student found = studentRepository.findByRegistrationNumber("2023001");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("João Silva");
        assertThat(found.getRegistrationNumber()).isEqualTo("2023001");
    }

    @Test
    void findByRegistrationNumber_WhenStudentDoesNotExist_ShouldReturnNull() {
        // given
        entityManager.persistAndFlush(student1);

        // when
        Student found = studentRepository.findByRegistrationNumber("9999999");

        // then
        assertThat(found).isNull();
    }

    @Test
    void findByNameContaining_WhenPartialMatch_ShouldReturnMatchingStudents() {
        // given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(student3);

        // when
        List<Student> found = studentRepository.findByNameContaining("João");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Student::getName)
                .containsExactlyInAnyOrder("João Silva", "João Oliveira");
    }

    @Test
    void findByNameContaining_WhenNoMatch_ShouldReturnEmptyList() {
        // given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // when
        List<Student> found = studentRepository.findByNameContaining("Pedro");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllStudents() {
        // given
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(student3);

        // when
        List<Student> allStudents = studentRepository.findAll();

        // then
        assertThat(allStudents).hasSize(3);
        assertThat(allStudents).extracting(Student::getName)
                .containsExactlyInAnyOrder("João Silva", "Maria Santos", "João Oliveira");
    }
}