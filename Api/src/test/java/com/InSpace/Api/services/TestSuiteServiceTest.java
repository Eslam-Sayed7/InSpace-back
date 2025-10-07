package com.InSpace.Api.services;

import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("TestSuiteService Unit Tests")
class TestSuiteServiceTest {

    @Mock
    private TestSuiteRepository testSuiteRepository;

    @InjectMocks
    private TestSuiteService testSuiteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should save test suite successfully")
    void testSave_Success() {
        // Arrange
        TestSuite testSuite = new TestSuite("Test Suite");
        when(testSuiteRepository.save(any(TestSuite.class))).thenReturn(testSuite);

        // Act
        TestSuite result = testSuiteService.save(testSuite);

        // Assert
        assertNotNull(result);
        assertEquals("Test Suite", result.getName());
        verify(testSuiteRepository, times(1)).save(testSuite);
    }

    @Test
    @DisplayName("Should create test suite with name only")
    void testCreateTestSuite_WithNameOnly_Success() {
        // Arrange
        String name = "New Test Suite";
        TestSuite testSuite = new TestSuite(name);
        when(testSuiteRepository.existsByName(name)).thenReturn(false);
        when(testSuiteRepository.save(any(TestSuite.class))).thenReturn(testSuite);

        // Act
        TestSuite result = testSuiteService.createTestSuite(name);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(testSuiteRepository, times(1)).existsByName(name);
        verify(testSuiteRepository, times(1)).save(any(TestSuite.class));
    }

    @Test
    @DisplayName("Should throw exception when creating test suite with duplicate name")
    void testCreateTestSuite_DuplicateName_ThrowsException() {
        // Arrange
        String name = "Duplicate Suite";
        when(testSuiteRepository.existsByName(name)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testSuiteService.createTestSuite(name);
        });

        assertEquals("Test suite with name 'Duplicate Suite' already exists", exception.getMessage());
        verify(testSuiteRepository, times(1)).existsByName(name);
        verify(testSuiteRepository, never()).save(any(TestSuite.class));
    }

    @Test
    @DisplayName("Should create test suite with name and description")
    void testCreateTestSuite_WithNameAndDescription_Success() {
        // Arrange
        String name = "Test Suite";
        String description = "Test Description";
        TestSuite testSuite = new TestSuite(name, description);
        when(testSuiteRepository.existsByName(name)).thenReturn(false);
        when(testSuiteRepository.save(any(TestSuite.class))).thenReturn(testSuite);

        // Act
        TestSuite result = testSuiteService.createTestSuite(name, description);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        verify(testSuiteRepository, times(1)).existsByName(name);
        verify(testSuiteRepository, times(1)).save(any(TestSuite.class));
    }

    @Test
    @DisplayName("Should find test suite by ID")
    void testFindById_Success() {
        // Arrange
        Long id = 1L;
        TestSuite testSuite = new TestSuite("Test Suite");
        when(testSuiteRepository.findById(id)).thenReturn(Optional.of(testSuite));

        // Act
        Optional<TestSuite> result = testSuiteService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Suite", result.get().getName());
        verify(testSuiteRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return empty when test suite not found by ID")
    void testFindById_NotFound() {
        // Arrange
        Long id = 999L;
        when(testSuiteRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<TestSuite> result = testSuiteService.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(testSuiteRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find test suite by name ignoring case")
    void testFindByNameIgnoreCase_Success() {
        // Arrange
        String name = "test suite";
        TestSuite testSuite = new TestSuite("Test Suite");
        when(testSuiteRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(testSuite));

        // Act
        Optional<TestSuite> result = testSuiteService.findByNameIgnoreCase(name);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Suite", result.get().getName());
        verify(testSuiteRepository, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Should find all test suites")
    void testFindAll_Success() {
        // Arrange
        List<TestSuite> testSuites = Arrays.asList(
            new TestSuite("Suite 1"),
            new TestSuite("Suite 2")
        );
        when(testSuiteRepository.findAll()).thenReturn(testSuites);

        // Act
        List<TestSuite> result = testSuiteService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(testSuiteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update test suite successfully")
    void testUpdate_Success() {
        // Arrange
        Long id = 1L;
        String newName = "Updated Suite";
        String newDescription = "Updated Description";
        TestSuite existingSuite = new TestSuite("Old Suite");
        
        when(testSuiteRepository.findById(id)).thenReturn(Optional.of(existingSuite));
        when(testSuiteRepository.existsByName(newName)).thenReturn(false);
        when(testSuiteRepository.save(any(TestSuite.class))).thenReturn(existingSuite);

        // Act
        TestSuite result = testSuiteService.update(id, newName, newDescription);

        // Assert
        assertNotNull(result);
        verify(testSuiteRepository, times(1)).findById(id);
        verify(testSuiteRepository, times(1)).existsByName(newName);
        verify(testSuiteRepository, times(1)).save(existingSuite);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent test suite")
    void testUpdate_NotFound_ThrowsException() {
        // Arrange
        Long id = 999L;
        when(testSuiteRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testSuiteService.update(id, "New Name", "New Description");
        });

        assertEquals("Test suite with ID 999 not found", exception.getMessage());
        verify(testSuiteRepository, times(1)).findById(id);
        verify(testSuiteRepository, never()).save(any(TestSuite.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate name")
    void testUpdate_DuplicateName_ThrowsException() {
        // Arrange
        Long id = 1L;
        String existingName = "Old Suite";
        String duplicateName = "Existing Suite";
        TestSuite existingSuite = new TestSuite(existingName);
        
        when(testSuiteRepository.findById(id)).thenReturn(Optional.of(existingSuite));
        when(testSuiteRepository.existsByName(duplicateName)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testSuiteService.update(id, duplicateName, "Description");
        });

        assertEquals("Test suite with name 'Existing Suite' already exists", exception.getMessage());
        verify(testSuiteRepository, times(1)).findById(id);
        verify(testSuiteRepository, times(1)).existsByName(duplicateName);
        verify(testSuiteRepository, never()).save(any(TestSuite.class));
    }

    @Test
    @DisplayName("Should delete test suite by ID successfully")
    void testDeleteById_Success() {
        // Arrange
        Long id = 1L;
        when(testSuiteRepository.existsById(id)).thenReturn(true);
        doNothing().when(testSuiteRepository).deleteById(id);

        // Act
        testSuiteService.deleteById(id);

        // Assert
        verify(testSuiteRepository, times(1)).existsById(id);
        verify(testSuiteRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent test suite")
    void testDeleteById_NotFound_ThrowsException() {
        // Arrange
        Long id = 999L;
        when(testSuiteRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testSuiteService.deleteById(id);
        });

        assertEquals("Test suite with ID 999 not found", exception.getMessage());
        verify(testSuiteRepository, times(1)).existsById(id);
        verify(testSuiteRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Should check if test suite exists by name")
    void testExistsByName() {
        // Arrange
        String name = "Test Suite";
        when(testSuiteRepository.existsByName(name)).thenReturn(true);

        // Act
        boolean result = testSuiteService.existsByName(name);

        // Assert
        assertTrue(result);
        verify(testSuiteRepository, times(1)).existsByName(name);
    }

    @Test
    @DisplayName("Should return false when test suite does not exist by name")
    void testExistsByName_NotExists() {
        // Arrange
        String name = "Non-existent Suite";
        when(testSuiteRepository.existsByName(name)).thenReturn(false);

        // Act
        boolean result = testSuiteService.existsByName(name);

        // Assert
        assertFalse(result);
        verify(testSuiteRepository, times(1)).existsByName(name);
    }
}
