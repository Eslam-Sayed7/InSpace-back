package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.services.TestSuiteService;
import com.InSpace.Api.services.dto.TestSuite.CreateTestSuiteRequest;
import com.InSpace.Api.services.dto.TestSuite.UpdateTestSuiteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("TestSuiteController Unit Tests")
class TestSuiteControllerTest {

    @Mock
    private TestSuiteService testSuiteService;

    @InjectMocks
    private TestSuiteController testSuiteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create test suite with name and description")
    void testCreateTestSuite_WithDescription_Success() {
        // Arrange
        CreateTestSuiteRequest request = new CreateTestSuiteRequest();
        request.setName("Test Suite");
        request.setDescription("Test Description");
        
        TestSuite testSuite = new TestSuite("Test Suite", "Test Description");
        when(testSuiteService.createTestSuite(anyString(), anyString())).thenReturn(testSuite);

        // Act
        ResponseEntity<?> response = testSuiteController.createTestSuite(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testSuiteService, times(1)).createTestSuite("Test Suite", "Test Description");
    }

    @Test
    @DisplayName("Should create test suite with name only")
    void testCreateTestSuite_WithoutDescription_Success() {
        // Arrange
        CreateTestSuiteRequest request = new CreateTestSuiteRequest();
        request.setName("Test Suite");
        request.setDescription("");
        
        TestSuite testSuite = new TestSuite("Test Suite");
        when(testSuiteService.createTestSuite(anyString())).thenReturn(testSuite);

        // Act
        ResponseEntity<?> response = testSuiteController.createTestSuite(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testSuiteService, times(1)).createTestSuite("Test Suite");
    }

    @Test
    @DisplayName("Should return conflict when creating test suite with duplicate name")
    void testCreateTestSuite_DuplicateName_ReturnsConflict() {
        // Arrange
        CreateTestSuiteRequest request = new CreateTestSuiteRequest();
        request.setName("Duplicate Suite");
        request.setDescription("Description");
        
        when(testSuiteService.createTestSuite(anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Test suite with name 'Duplicate Suite' already exists"));

        // Act
        ResponseEntity<?> response = testSuiteController.createTestSuite(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(testSuiteService, times(1)).createTestSuite(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get all test suites")
    void testGetAllTestSuites_Success() {
        // Arrange
        List<TestSuite> testSuites = Arrays.asList(
            new TestSuite("Suite 1"),
            new TestSuite("Suite 2")
        );
        when(testSuiteService.findAll()).thenReturn(testSuites);

        // Act
        ResponseEntity<List<TestSuite>> response = testSuiteController.getAllTestSuites();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(testSuiteService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get test suite by ID")
    void testGetTestSuiteById_Success() {
        // Arrange
        Long id = 1L;
        TestSuite testSuite = new TestSuite("Test Suite");
        when(testSuiteService.findById(id)).thenReturn(Optional.of(testSuite));

        // Act
        ResponseEntity<?> response = testSuiteController.getTestSuiteById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testSuiteService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return not found when test suite does not exist by ID")
    void testGetTestSuiteById_NotFound() {
        // Arrange
        Long id = 999L;
        when(testSuiteService.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = testSuiteController.getTestSuiteById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testSuiteService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should get test suite by name")
    void testGetTestSuiteByName_Success() {
        // Arrange
        String name = "Test Suite";
        TestSuite testSuite = new TestSuite(name);
        when(testSuiteService.findByNameIgnoreCase(name)).thenReturn(Optional.of(testSuite));

        // Act
        ResponseEntity<?> response = testSuiteController.getTestSuiteByName(name);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testSuiteService, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Should return not found when test suite does not exist by name")
    void testGetTestSuiteByName_NotFound() {
        // Arrange
        String name = "Non-existent Suite";
        when(testSuiteService.findByNameIgnoreCase(name)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = testSuiteController.getTestSuiteByName(name);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testSuiteService, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Should update test suite successfully")
    void testUpdateTestSuite_Success() {
        // Arrange
        Long id = 1L;
        UpdateTestSuiteRequest request = new UpdateTestSuiteRequest();
        request.setName("Updated Suite");
        request.setDescription("Updated Description");
        
        TestSuite updatedSuite = new TestSuite("Updated Suite", "Updated Description");
        when(testSuiteService.update(id, request.getName(), request.getDescription()))
            .thenReturn(updatedSuite);

        // Act
        ResponseEntity<?> response = testSuiteController.updateTestSuite(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testSuiteService, times(1)).update(id, request.getName(), request.getDescription());
    }

    @Test
    @DisplayName("Should return bad request when updating test suite fails")
    void testUpdateTestSuite_Failure() {
        // Arrange
        Long id = 1L;
        UpdateTestSuiteRequest request = new UpdateTestSuiteRequest();
        request.setName("Updated Suite");
        request.setDescription("Updated Description");
        
        when(testSuiteService.update(id, request.getName(), request.getDescription()))
            .thenThrow(new IllegalArgumentException("Test suite not found"));

        // Act
        ResponseEntity<?> response = testSuiteController.updateTestSuite(id, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(testSuiteService, times(1)).update(id, request.getName(), request.getDescription());
    }

    @Test
    @DisplayName("Should delete test suite successfully")
    void testDeleteTestSuite_Success() {
        // Arrange
        Long id = 1L;
        doNothing().when(testSuiteService).deleteById(id);

        // Act
        ResponseEntity<?> response = testSuiteController.deleteTestSuite(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(testSuiteService, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent test suite")
    void testDeleteTestSuite_NotFound() {
        // Arrange
        Long id = 999L;
        doThrow(new IllegalArgumentException("Test suite not found"))
            .when(testSuiteService).deleteById(id);

        // Act
        ResponseEntity<?> response = testSuiteController.deleteTestSuite(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testSuiteService, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should check if test suite exists by name")
    void testCheckTestSuiteExists_Exists() {
        // Arrange
        String name = "Test Suite";
        when(testSuiteService.existsByName(name)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = testSuiteController.checkTestSuiteExists(name);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(testSuiteService, times(1)).existsByName(name);
    }

    @Test
    @DisplayName("Should check if test suite does not exist by name")
    void testCheckTestSuiteExists_NotExists() {
        // Arrange
        String name = "Non-existent Suite";
        when(testSuiteService.existsByName(name)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = testSuiteController.checkTestSuiteExists(name);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(testSuiteService, times(1)).existsByName(name);
    }
}
