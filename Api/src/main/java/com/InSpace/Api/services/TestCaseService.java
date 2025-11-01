package com.InSpace.Api.services;

import com.InSpace.Api.domain.TestCase;
import com.InSpace.Api.domain.Module;
import com.InSpace.Api.infra.repository.TestCaseRepository;
import com.InSpace.Api.infra.repository.ModuleRepository;
import com.InSpace.Api.infra.Specifications.TestCaseSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ModuleRepository moduleRepository;

    @Autowired
    public TestCaseService(TestCaseRepository testCaseRepository,
            ModuleRepository moduleRepository) {
        this.testCaseRepository = testCaseRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional(readOnly = true)
    public List<Short> findAvailablePriorities() {
        return testCaseRepository.findDistinctPriorities();
    }

    @Transactional
    public TestCase createTestcase(String name, String description, Short priority, Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module with ID " + moduleId + " not found"));

        if (testCaseRepository.existsByNameAndModule(name, module)) {
            throw new IllegalArgumentException("Testcase with name '" + name + "' already exists in this module");
        }

        TestCase testcase = new TestCase(name, description, priority, module);
        return testCaseRepository.save(testcase);
    }

    @Transactional(readOnly = true)
    public Optional<TestCase> findById(Long id) {
        return testCaseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<TestCase> findByModule(Long moduleId, int page, int size, String sortField, String sortDir) {
        Pageable pageable;
        if (sortField == null || sortField.isEmpty() || sortDir == null || sortDir.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.unsorted());
        } else {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();
            pageable = PageRequest.of(page, size, sort);
        }
        return testCaseRepository.findAll(TestCaseSpecifications.hasModuleId(moduleId), pageable);
    }

    @Transactional(readOnly = true)
    public Page<TestCase> findByPriority(Short priority, int page, int size, String sortField, String sortDir) {
        Sort sort;
        if (sortField == null || sortField.isEmpty() || sortDir == null || sortDir.isEmpty()) {
            sort = Sort.unsorted();
        } else {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return testCaseRepository.findAll(TestCaseSpecifications.hasPriority(priority), pageable);
    }

    @Transactional(readOnly = true)
    public Page<TestCase> searchTestcasesInModule(
            Long moduleId,
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
        Specification<TestCase> spec = TestCaseSpecifications.hasModuleId(moduleId)
                .and(TestCaseSpecifications.nameOrDescriptionContains(keyword));

        return testCaseRepository.findAll(spec, pageable);
    }

    Boolean existsByNameAndModule(String name, Module module) {
        return testCaseRepository.existsByNameAndModule(name, module);
    }
}
