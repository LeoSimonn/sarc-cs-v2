package com.example.studentsubjectapi.service;

import com.example.studentsubjectapi.model.Subject;
import com.example.studentsubjectapi.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject subject1;
    private Subject subject2;
    private Subject subject3;

    @BeforeEach
    void setUp() {
        subject1 = new Subject("MAT001", "Matemática Básica", "A");
        subject1.setId(1L);
        
        subject2 = new Subject("FIS001", "Física I", "B");
        subject2.setId(2L);
        
        subject3 = new Subject("MAT001", "Matemática Básica", "C");
        subject3.setId(3L);
    }

    @Test
    void addSubject_ShouldReturnSavedSubject() {
        // Given
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject1);

        // When
        Subject result = subjectService.addSubject(subject1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSubjectCode()).isEqualTo("MAT001");
        assertThat(result.getSubjectName()).isEqualTo("Matemática Básica");
        assertThat(result.getSchedule()).isEqualTo("A");
        verify(subjectRepository, times(1)).save(subject1);
    }

    @Test
    void getAllSubjects_ShouldReturnAllSubjects() {
        // Given
        List<Subject> subjects = Arrays.asList(subject1, subject2, subject3);
        when(subjectRepository.findAll()).thenReturn(subjects);

        // When
        List<Subject> result = subjectService.getAllSubjects();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(subject1, subject2, subject3);
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void getSubjectByCode_WhenSubjectExists_ShouldReturnSubject() {
        // Given
        String subjectCode = "MAT001";
        when(subjectRepository.findBySubjectCode(subjectCode)).thenReturn(subject1);

        // When
        Subject result = subjectService.getSubjectByCode(subjectCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSubjectCode()).isEqualTo("MAT001");
        assertThat(result.getSubjectName()).isEqualTo("Matemática Básica");
        assertThat(result.getSchedule()).isEqualTo("A");
        verify(subjectRepository, times(1)).findBySubjectCode(subjectCode);
    }

    @Test
    void getSubjectByCode_WhenSubjectDoesNotExist_ShouldReturnNull() {
        // Given
        String subjectCode = "INV001";
        when(subjectRepository.findBySubjectCode(subjectCode)).thenReturn(null);

        // When
        Subject result = subjectService.getSubjectByCode(subjectCode);

        // Then
        assertThat(result).isNull();
        verify(subjectRepository, times(1)).findBySubjectCode(subjectCode);
    }

    @Test
    void getSubjectsByName_WhenSubjectsExist_ShouldReturnMatchingSubjects() {
        // Given
        String subjectName = "Matemática";
        List<Subject> matchingSubjects = Arrays.asList(subject1, subject3);
        when(subjectRepository.findBySubjectNameContaining(subjectName)).thenReturn(matchingSubjects);

        // When
        List<Subject> result = subjectService.getSubjectsByName(subjectName);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(subject1, subject3);
        verify(subjectRepository, times(1)).findBySubjectNameContaining(subjectName);
    }

    @Test
    void addSubject_WithDifferentSchedules_ShouldSaveBothSubjects() {
        // Given
        Subject matA = new Subject("MAT001", "Matemática Básica", "A");
        Subject matB = new Subject("MAT001", "Matemática Básica", "B");
        
        when(subjectRepository.save(any(Subject.class)))
            .thenReturn(matA)
            .thenReturn(matB);

        // When
        Subject result1 = subjectService.addSubject(matA);
        Subject result2 = subjectService.addSubject(matB);

        // Then
        assertThat(result1.getSchedule()).isEqualTo("A");
        assertThat(result2.getSchedule()).isEqualTo("B");
        assertThat(result1.getSubjectCode()).isEqualTo(result2.getSubjectCode());
        assertThat(result1.getSubjectName()).isEqualTo(result2.getSubjectName());
        verify(subjectRepository, times(2)).save(any(Subject.class));
    }
}
