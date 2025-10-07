package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.services.TestScenarioService;
import com.InSpace.Api.services.dto.TestScenario.Requests.CreateTestScenarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("TestScenarioController Unit Tests")
class TestScenarioControllerTest {

    @Mock
    private TestScenarioService testScenarioService;

    @InjectMocks
    private TestScenarioController testScenarioController;

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
        CreateTestScenarioRequest request = new CreateTestScenarioRequest();
        request.setName("New Scenario");
        request.setDescription("Description");
        request.setPriority((short) 1);
        request.setSuiteId(1L);
        request.setTagNames(new HashSet<>());
        
        when(testScenarioService.createTestScenarioWithTags(
            anyString(), anyString(), anyShort(), anyLong(), any()))
            .thenReturn(testScenario);

        // Act
        ResponseEntity<?> response = testScenarioController.createTestScenario(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testScenarioService, times(1))
            .createTestScenarioWithTags(anyString(), anyString(), anyShort(), anyLong(), any());
    }

    @Test
    @DisplayName("Should return bad request when creating scenario fails")
    void testCreateTestScenario_Failure() {
        // Arrange
        CreateTestScenarioRequest request = new CreateTestScenarioRequest();
        request.setName("Scenario");
        request.setDescription("Description");
        request.setPriority((short) 1);
        request.setSuiteId(1L);
        
        when(testScenarioService.createTestScenarioWithTags(
            anyString(), anyString(), anyShort(), anyLong(), any()))
            .thenThrow(new IllegalArgumentException("Error creating scenario"));

        // Act
        ResponseEntity<?> response = testScenarioController.createTestScenario(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(testScenarioService, times(1))
            .createTestScenarioWithTags(anyString(), anyString(), anyShort(), anyLong(), any());
    }

    @Test
    @DisplayName("Should get test scenario by ID")
    void testGetTestScenarioById_Success() {
        // Arrange
        Long id = 1L;
        when(testScenarioService.findById(id)).thenReturn(Optional.of(testScenario));

        // Act
        ResponseEntity<?> response = testScenarioController.getTestScenarioById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testScenarioService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return not found when scenario does not exist")
    void testGetTestScenarioById_NotFound() {
        // Arrange
        Long id = 999L;
        when(testScenarioService.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = testScenarioController.getTestScenarioById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testScenarioService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should get scenarios by suite with pagination")
    void testGetBySuite_Success() {
        // Arrange
        Long suiteId = 1L;
        List<TestScenario> scenarios = Arrays.asList(testScenario);
        Page<TestScenario> page = new PageImpl<>(scenarios);
        
        when(testScenarioService.findBySuite(anyLong(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(page);

        // Act
        ResponseEntity<?> response = testScenarioController.getBySuite(suiteId, 0, 20, "name", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testScenarioService, times(1))
            .findBySuite(eq(suiteId), eq(0), eq(20), eq("name"), eq("asc"));
    }

    @Test
    @DisplayName("Should get scenarios by priority with pagination")
    void testGetByPriority_Success() {
        // Arrange
        Short priority = 1;
        List<TestScenario> scenarios = Arrays.asList(testScenario);
        Page<TestScenario> page = new PageImpl<>(scenarios);
        
        when(testScenarioService.findByPriority(anyShort(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(page);

        // Act
        ResponseEntity<?> response = testScenarioController.getByPriority(priority, 0, 20, "name", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(testScenarioService, times(1))
            .findByPriority(eq(priority), eq(0), eq(20), eq("name"), eq("asc"));
    }
}
