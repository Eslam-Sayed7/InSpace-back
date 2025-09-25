package com.InSpace.Api.services;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.domain.Tag;
import com.InSpace.Api.infra.repository.TestScenarioRepository;
import com.InSpace.Api.infra.repository.TestSuiteRepository;
import com.InSpace.Api.infra.Specifications.TestScenarioSpecifications;
import com.InSpace.Api.infra.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public TestScenario createTestScenario(String name, String description, Short priority, Long suiteId) {
        TestSuite testSuite = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite with ID " + suiteId + " not found"));

        if (testScenarioRepository.existsByNameAndTestSuite(name, testSuite)) {
            throw new IllegalArgumentException("Test scenario with name '" + name + "' already exists in this suite");
        }

        TestScenario scenario = new TestScenario(name, description, priority, testSuite);
        return testScenarioRepository.save(scenario);
    }

    @Transactional
    public TestScenario createTestScenarioWithTags(String name, String description, Short priority,
            Long suiteId, Set<String> tagNames) {
        TestScenario scenario = createTestScenario(name, description, priority, suiteId);

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                scenario.addTag(tag);
            }
            testScenarioRepository.save(scenario);
        }
        return scenario;
    }

    @Transactional(readOnly = true)
    public Optional<TestScenario> findById(Long id) {
        return testScenarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<TestScenario> findBySuite(Long suiteId, int page, int size, String sortField, String sortDir) {
        Pageable pageable;
        if (sortField == null || sortField.isEmpty() || sortDir == null || sortDir.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.unsorted());
        } else {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();
            pageable = PageRequest.of(page, size, sort);
        }
        return testScenarioRepository.findAll(TestScenarioSpecifications.hasSuiteId(suiteId), pageable);
    }

    @Transactional(readOnly = true)
    public Page<TestScenario> findByPriority(Short priority, int page, int size, String sortField, String sortDir) {
        Sort sort;
        if (sortField == null || sortField.isEmpty() || sortDir == null || sortDir.isEmpty()) {
            sort = Sort.unsorted();
        } else {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return testScenarioRepository.findAll(TestScenarioSpecifications.hasPriority(priority), pageable);
    }

    @Transactional(readOnly = true)
    public Page<TestScenario> findByTagNames(Set<String> tagNames, int page, int size, String sortField,
            String sortDir) {
        Sort sort;
        if (sortField == null || sortField.isEmpty() || sortDir == null || sortDir.isEmpty()) {
            sort = Sort.unsorted();
        } else {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return testScenarioRepository.findDistinctByTags_NameIn(tagNames, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TestScenario> searchScenariosInSuite(
            Long suitId,
            String keyword,
            int page,
            int size,
            String sortField,
            String sortDir) {
        Sort sort;
        if (sortField == null || sortField.isEmpty() || sortDir == null || sortDir.isEmpty()) {
            sort = Sort.unsorted();
        } else {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Specification<TestScenario> spec = TestScenarioSpecifications.hasSuiteId(suitId)
                .and(TestScenarioSpecifications.nameOrDescriptionContains(keyword));

        return testScenarioRepository.findAll(spec, pageable);
    }

    Boolean existsByNameAndTestSuite(String name, TestSuite testSuite) {
        return testScenarioRepository.existsByNameAndTestSuite(name, testSuite);
    }
}