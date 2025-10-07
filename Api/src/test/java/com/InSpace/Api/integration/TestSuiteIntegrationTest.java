package com.InSpace.Api.integration;

import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import com.InSpace.Api.services.TestSuiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@DisplayName("TestSuite Integration Tests")
class TestSuiteIntegrationTest {

    @Autowired
    private TestSuiteService testSuiteService;

    @Autowired
    private TestSuiteRepository testSuiteRepository;

    @BeforeEach
    void setUp() {
        testSuiteRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create and retrieve test suite")
    void testCreateAndRetrieveTestSuite() {
        // Arrange
        String name = "Integration Test Suite";
        String description = "Integration Test Description";

        // Act
        TestSuite created = testSuiteService.createTestSuite(name, description);
        Optional<TestSuite> retrieved = testSuiteService.findById(created.getSuiteId());

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals(name, retrieved.get().getName());
        assertEquals(description, retrieved.get().getDescription());
    }

    @Test
    @DisplayName("Should prevent duplicate suite names")
    void testPreventDuplicateSuiteNames() {
        // Arrange
        String name = "Duplicate Suite";
        testSuiteService.createTestSuite(name);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            testSuiteService.createTestSuite(name);
        });
    }

    @Test
    @DisplayName("Should update test suite")
    void testUpdateTestSuite() {
        // Arrange
        TestSuite created = testSuiteService.createTestSuite("Original Name", "Original Description");
        Long id = created.getSuiteId();

        // Act
        TestSuite updated = testSuiteService.update(id, "Updated Name", "Updated Description");

        // Assert
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    @DisplayName("Should delete test suite")
    void testDeleteTestSuite() {
        // Arrange
        TestSuite created = testSuiteService.createTestSuite("To Delete");
        Long id = created.getSuiteId();

        // Act
        testSuiteService.deleteById(id);

        // Assert
        Optional<TestSuite> deleted = testSuiteService.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should find all test suites")
    void testFindAllTestSuites() {
        // Arrange
        testSuiteService.createTestSuite("Suite 1");
        testSuiteService.createTestSuite("Suite 2");
        testSuiteService.createTestSuite("Suite 3");

        // Act
        List<TestSuite> suites = testSuiteService.findAll();

        // Assert
        assertEquals(3, suites.size());
    }

    @Test
    @DisplayName("Should find test suite by name ignoring case")
    void testFindByNameIgnoreCase() {
        // Arrange
        testSuiteService.createTestSuite("CaseSensitive Suite");

        // Act
        Optional<TestSuite> found = testSuiteService.findByNameIgnoreCase("casesensitive suite");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("CaseSensitive Suite", found.get().getName());
    }

    @Test
    @DisplayName("Should check if test suite exists by name")
    void testExistsByName() {
        // Arrange
        testSuiteService.createTestSuite("Existing Suite");

        // Act
        boolean exists = testSuiteService.existsByName("Existing Suite");
        boolean notExists = testSuiteService.existsByName("Non-existing Suite");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }
}
