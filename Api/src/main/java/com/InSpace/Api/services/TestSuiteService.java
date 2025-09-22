package com.InSpace.Api.services;

import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestSuiteService {

    private final TestSuiteRepository testSuiteRepository;

    @Autowired
    public TestSuiteService(TestSuiteRepository testSuiteRepository) {
        this.testSuiteRepository = testSuiteRepository;
    }

    public TestSuite save(TestSuite testSuite) {
        return testSuiteRepository.save(testSuite);
    }

    public TestSuite createTestSuite(String name) {
        if (testSuiteRepository.existsByName(name)) {
            throw new IllegalArgumentException("Test suite with name '" + name + "' already exists");
        }
        TestSuite testSuite = new TestSuite(name);
        return testSuiteRepository.save(testSuite);
    }

    public TestSuite createTestSuite(String name, String description) {
        if (testSuiteRepository.existsByName(name)) {
            throw new IllegalArgumentException("Test suite with name '" + name + "' already exists");
        }
        TestSuite testSuite = new TestSuite(name, description);
        return testSuiteRepository.save(testSuite);
    }

    @Transactional(readOnly = true)
    public Optional<TestSuite> findById(Long id) {
        return testSuiteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<TestSuite> findByNameIgnoreCase(String name) {
        return testSuiteRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<TestSuite> findAll() {
        return testSuiteRepository.findAll();
    }

    public TestSuite update(Long id, String name, String description) {
        TestSuite testSuite = testSuiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test suite with ID " + id + " not found"));

        // Check if new name already exists
        if (!testSuite.getName().equals(name) && testSuiteRepository.existsByName(name)) {
            throw new IllegalArgumentException("Test suite with name '" + name + "' already exists");
        }

        testSuite.setName(name);
        testSuite.setDescription(description);
        return testSuiteRepository.save(testSuite);
    }

    public void deleteById(Long id) {
        if (!testSuiteRepository.existsById(id)) {
            throw new IllegalArgumentException("Test suite with ID " + id + " not found");
        }
        testSuiteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return testSuiteRepository.existsByName(name);
    }
}