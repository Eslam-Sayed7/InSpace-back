package com.InSpace.Api.services;

import com.InSpace.Api.domain.Tag;
import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.infra.repository.TagRepository;
import com.InSpace.Api.infra.repository.TestScenarioRepository;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("TestScenarioService Unit Tests")
class TestScenarioServiceTest {

    @Mock
    private TestScenarioRepository testScenarioRepository;

    @Mock
    private TestSuiteRepository testSuiteRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TestScenarioService testScenarioService;

    private TestSuite testSuite;
    private TestScenario testScenario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testSuite = new TestSuite("Test Suite");
        testScenario = new TestScenario("Test Scenario", "Description", (short) 1, testSuite);
    }

    @Test
    @DisplayName("Should create test scenario successfully")
    void testCreateTestScenario_Success() {
        // Arrange
        Long suiteId = 1L;
        String name = "New Scenario";
        String description = "New Description";
        Short priority = 1;
        
        when(testSuiteRepository.findById(suiteId)).thenReturn(Optional.of(testSuite));
        when(testScenarioRepository.existsByNameAndTestSuite(name, testSuite)).thenReturn(false);
        when(testScenarioRepository.save(any(TestScenario.class))).thenReturn(testScenario);

        // Act
        TestScenario result = testScenarioService.createTestScenario(name, description, priority, suiteId);

        // Assert
        assertNotNull(result);
        verify(testSuiteRepository, times(1)).findById(suiteId);
        verify(testScenarioRepository, times(1)).existsByNameAndTestSuite(name, testSuite);
        verify(testScenarioRepository, times(1)).save(any(TestScenario.class));
    }

    @Test
    @DisplayName("Should throw exception when test suite not found")
    void testCreateTestScenario_SuiteNotFound_ThrowsException() {
        // Arrange
        Long suiteId = 999L;
        when(testSuiteRepository.findById(suiteId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testScenarioService.createTestScenario("Name", "Desc", (short) 1, suiteId);
        });

        assertEquals("Test suite with ID 999 not found", exception.getMessage());
        verify(testSuiteRepository, times(1)).findById(suiteId);
        verify(testScenarioRepository, never()).save(any(TestScenario.class));
    }

    @Test
    @DisplayName("Should throw exception when scenario name already exists in suite")
    void testCreateTestScenario_DuplicateName_ThrowsException() {
        // Arrange
        Long suiteId = 1L;
        String name = "Existing Scenario";
        
        when(testSuiteRepository.findById(suiteId)).thenReturn(Optional.of(testSuite));
        when(testScenarioRepository.existsByNameAndTestSuite(name, testSuite)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testScenarioService.createTestScenario(name, "Desc", (short) 1, suiteId);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(testSuiteRepository, times(1)).findById(suiteId);
        verify(testScenarioRepository, times(1)).existsByNameAndTestSuite(name, testSuite);
        verify(testScenarioRepository, never()).save(any(TestScenario.class));
    }

    @Test
    @DisplayName("Should create test scenario with tags")
    void testCreateTestScenarioWithTags_Success() {
        // Arrange
        Long suiteId = 1L;
        String name = "Scenario with Tags";
        Set<String> tagNames = new HashSet<>(Arrays.asList("tag1", "tag2"));
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        
        when(testSuiteRepository.findById(suiteId)).thenReturn(Optional.of(testSuite));
        when(testScenarioRepository.existsByNameAndTestSuite(name, testSuite)).thenReturn(false);
        when(testScenarioRepository.save(any(TestScenario.class))).thenReturn(testScenario);
        when(tagRepository.findByNameIgnoreCase("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByNameIgnoreCase("tag2")).thenReturn(Optional.of(tag2));

        // Act
        TestScenario result = testScenarioService.createTestScenarioWithTags(
            name, "Description", (short) 1, suiteId, tagNames);

        // Assert
        assertNotNull(result);
        verify(testScenarioRepository, times(2)).save(any(TestScenario.class));
        verify(tagRepository, times(1)).findByNameIgnoreCase("tag1");
        verify(tagRepository, times(1)).findByNameIgnoreCase("tag2");
    }

    @Test
    @DisplayName("Should create test scenario with new tags")
    void testCreateTestScenarioWithTags_NewTags_Success() {
        // Arrange
        Long suiteId = 1L;
        String name = "Scenario with New Tags";
        Set<String> tagNames = new HashSet<>(Arrays.asList("newtag"));
        Tag newTag = new Tag("newtag");
        
        when(testSuiteRepository.findById(suiteId)).thenReturn(Optional.of(testSuite));
        when(testScenarioRepository.existsByNameAndTestSuite(name, testSuite)).thenReturn(false);
        when(testScenarioRepository.save(any(TestScenario.class))).thenReturn(testScenario);
        when(tagRepository.findByNameIgnoreCase("newtag")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

        // Act
        TestScenario result = testScenarioService.createTestScenarioWithTags(
            name, "Description", (short) 1, suiteId, tagNames);

        // Assert
        assertNotNull(result);
        verify(tagRepository, times(1)).findByNameIgnoreCase("newtag");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("Should find test scenario by ID")
    void testFindById_Success() {
        // Arrange
        Long id = 1L;
        when(testScenarioRepository.findById(id)).thenReturn(Optional.of(testScenario));

        // Act
        Optional<TestScenario> result = testScenarioService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testScenario, result.get());
        verify(testScenarioRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return empty when scenario not found by ID")
    void testFindById_NotFound() {
        // Arrange
        Long id = 999L;
        when(testScenarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<TestScenario> result = testScenarioService.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(testScenarioRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find scenarios by suite with pagination")
    void testFindBySuite_Success() {
        // Arrange
        Long suiteId = 1L;
        List<TestScenario> scenarios = Arrays.asList(testScenario);
        Page<TestScenario> page = new PageImpl<>(scenarios);
        
        when(testScenarioRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        // Act
        Page<TestScenario> result = testScenarioService.findBySuite(suiteId, 0, 10, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(testScenarioRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should find scenarios by priority with pagination")
    void testFindByPriority_Success() {
        // Arrange
        Short priority = 1;
        List<TestScenario> scenarios = Arrays.asList(testScenario);
        Page<TestScenario> page = new PageImpl<>(scenarios);
        
        when(testScenarioRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        // Act
        Page<TestScenario> result = testScenarioService.findByPriority(priority, 0, 10, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(testScenarioRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should find scenarios by tag names with pagination")
    void testFindByTagNames_Success() {
        // Arrange
        Set<String> tagNames = new HashSet<>(Arrays.asList("tag1"));
        List<TestScenario> scenarios = Arrays.asList(testScenario);
        Page<TestScenario> page = new PageImpl<>(scenarios);
        
        when(testScenarioRepository.findDistinctByTags_NameIn(eq(tagNames), any(Pageable.class)))
            .thenReturn(page);

        // Act
        Page<TestScenario> result = testScenarioService.findByTagNames(tagNames, 0, 10, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(testScenarioRepository, times(1))
            .findDistinctByTags_NameIn(eq(tagNames), any(Pageable.class));
    }

    @Test
    @DisplayName("Should search scenarios in suite with keyword")
    void testSearchScenariosInSuite_Success() {
        // Arrange
        Long suiteId = 1L;
        String keyword = "test";
        List<TestScenario> scenarios = Arrays.asList(testScenario);
        Page<TestScenario> page = new PageImpl<>(scenarios);
        
        when(testScenarioRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        // Act
        Page<TestScenario> result = testScenarioService.searchScenariosInSuite(
            suiteId, keyword, 0, 10, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(testScenarioRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should check if scenario exists by name and test suite")
    void testExistsByNameAndTestSuite() {
        // Arrange
        String name = "Test Scenario";
        when(testScenarioRepository.existsByNameAndTestSuite(name, testSuite)).thenReturn(true);

        // Act
        Boolean result = testScenarioService.existsByNameAndTestSuite(name, testSuite);

        // Assert
        assertTrue(result);
        verify(testScenarioRepository, times(1)).existsByNameAndTestSuite(name, testSuite);
    }
}
