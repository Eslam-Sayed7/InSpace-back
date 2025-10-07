package com.InSpace.Api.integration;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.infra.repository.TestScenarioRepository;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import com.InSpace.Api.services.TestScenarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@DisplayName("TestScenario Integration Tests")
class TestScenarioIntegrationTest {

    @Autowired
    private TestScenarioService testScenarioService;

    @Autowired
    private TestScenarioRepository testScenarioRepository;

    @Autowired
    private TestSuiteRepository testSuiteRepository;

    private TestSuite testSuite;

    @BeforeEach
    void setUp() {
        testScenarioRepository.deleteAll();
        testSuiteRepository.deleteAll();
        testSuite = testSuiteRepository.save(new TestSuite("Test Suite", "Description"));
    }

    @Test
    @DisplayName("Should create and retrieve test scenario")
    void testCreateAndRetrieveTestScenario() {
        // Arrange
        String name = "Integration Test Scenario";
        String description = "Integration Test Description";
        Short priority = 1;

        // Act
        TestScenario created = testScenarioService.createTestScenario(
            name, description, priority, testSuite.getSuiteId());
        Optional<TestScenario> retrieved = testScenarioService.findById(created.getScenarioId());

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals(name, retrieved.get().getName());
        assertEquals(description, retrieved.get().getDescription());
        assertEquals(priority, retrieved.get().getPriority());
    }

    @Test
    @DisplayName("Should create test scenario with tags")
    void testCreateTestScenarioWithTags() {
        // Arrange
        String name = "Scenario with Tags";
        Set<String> tagNames = new HashSet<>();
        tagNames.add("smoke");
        tagNames.add("regression");

        // Act
        TestScenario created = testScenarioService.createTestScenarioWithTags(
            name, "Description", (short) 1, testSuite.getSuiteId(), tagNames);

        // Assert
        assertNotNull(created);
        assertEquals(name, created.getName());
        assertEquals(2, created.getTags().size());
    }

    @Test
    @DisplayName("Should prevent duplicate scenario names in same suite")
    void testPreventDuplicateScenarioNamesInSuite() {
        // Arrange
        String name = "Duplicate Scenario";
        testScenarioService.createTestScenario(name, "Desc", (short) 1, testSuite.getSuiteId());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            testScenarioService.createTestScenario(name, "Desc", (short) 1, testSuite.getSuiteId());
        });
    }

    @Test
    @DisplayName("Should find scenarios by suite with pagination")
    void testFindScenariosBySuite() {
        // Arrange
        testScenarioService.createTestScenario("Scenario 1", "Desc 1", (short) 1, testSuite.getSuiteId());
        testScenarioService.createTestScenario("Scenario 2", "Desc 2", (short) 2, testSuite.getSuiteId());
        testScenarioService.createTestScenario("Scenario 3", "Desc 3", (short) 3, testSuite.getSuiteId());

        // Act
        Page<TestScenario> page = testScenarioService.findBySuite(
            testSuite.getSuiteId(), 0, 10, "name", "asc");

        // Assert
        assertEquals(3, page.getContent().size());
    }

    @Test
    @DisplayName("Should find scenarios by priority")
    void testFindScenariosByPriority() {
        // Arrange
        testScenarioService.createTestScenario("High Priority 1", "Desc", (short) 1, testSuite.getSuiteId());
        testScenarioService.createTestScenario("High Priority 2", "Desc", (short) 1, testSuite.getSuiteId());
        testScenarioService.createTestScenario("Low Priority", "Desc", (short) 3, testSuite.getSuiteId());

        // Act
        Page<TestScenario> highPriority = testScenarioService.findByPriority((short) 1, 0, 10, "name", "asc");

        // Assert
        assertEquals(2, highPriority.getContent().size());
    }

    @Test
    @DisplayName("Should search scenarios in suite by keyword")
    void testSearchScenariosInSuite() {
        // Arrange
        testScenarioService.createTestScenario("Login Test", "Test login", (short) 1, testSuite.getSuiteId());
        testScenarioService.createTestScenario("Logout Test", "Test logout", (short) 1, testSuite.getSuiteId());
        testScenarioService.createTestScenario("Registration", "Test registration", (short) 2, testSuite.getSuiteId());

        // Act
        Page<TestScenario> results = testScenarioService.searchScenariosInSuite(
            testSuite.getSuiteId(), "Test", 0, 10, "name", "asc");

        // Assert
        assertEquals(3, results.getContent().size());
    }

    @Test
    @DisplayName("Should throw exception when creating scenario with non-existent suite")
    void testCreateScenarioWithNonExistentSuite() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            testScenarioService.createTestScenario("Scenario", "Desc", (short) 1, 999L);
        });
    }
}
