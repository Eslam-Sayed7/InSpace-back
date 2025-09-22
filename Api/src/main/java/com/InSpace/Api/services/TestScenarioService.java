package com.InSpace.Api.services;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.domain.Tag;
import com.InSpace.Api.infra.repository.TestScenarioRepository;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import com.InSpace.Api.infra.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TestScenarioService {

    private final TestScenarioRepository testScenarioRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final TagRepository tagRepository;

    @Autowired
    public TestScenarioService(TestScenarioRepository testScenarioRepository,
            TestSuiteRepository testSuiteRepository,
            TagRepository tagRepository) {
        this.testScenarioRepository = testScenarioRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Create a new test scenario
     */
    public TestScenario createTestScenario(String name, String description, Short priority, Long suiteId) {
        TestSuite testSuite = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite with ID " + suiteId + " not found"));

        if (testScenarioRepository.existsByNameAndTestSuite(name, testSuite)) {
            throw new IllegalArgumentException("Test scenario with name '" + name + "' already exists in this suite");
        }

        TestScenario scenario = new TestScenario(name, description, priority, testSuite);
        return testScenarioRepository.save(scenario);
    }

    public TestScenario createTestScenarioWithTags(String name, String description, Short priority,
            Long suiteId, Set<String> tagNames) {
        TestScenario scenario = createTestScenario(name, description, priority, suiteId);

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                scenario.addTag(tag);
            }
            testScenarioRepository.save(scenario); // Save again to persist tag associations
        }

        return scenario;
    }

    @Transactional(readOnly = true)
    public Optional<TestScenario> findById(Long id) {
        return testScenarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<TestScenario> findByTestSuite(Long suiteId) {
        return testScenarioRepository.findByTestSuiteSuiteId(suiteId);
    }

    @Transactional(readOnly = true)
    public List<TestScenario> findByPriority(Short priority) {
        return testScenarioRepository.findByPriority(priority);
    }

    @Transactional(readOnly = true)
    public List<TestScenario> findByTagNames(List<String> tagNames) {
        return testScenarioRepository.findByTagNames(tagNames);
    }

    @Transactional(readOnly = true)
    public List<TestScenario> searchScenarios(String searchTerm) {
        return testScenarioRepository.findByNameOrDescriptionContaining(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<TestScenario> searchScenariosInSuite(Long suiteId, String searchTerm) {
        return testScenarioRepository.findBySuiteIdAndSearch(suiteId, searchTerm);
    }
}