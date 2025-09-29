package com.InSpace.Api.services;

import com.InSpace.Api.domain.ActionType;
import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestStep;
import com.InSpace.Api.infra.repository.TestStepRepository;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TestStepService {

    private final TestStepRepository testStepRepository;
    private final ActionTypeService actionTypeService;

    @Autowired
    public TestStepService(TestStepRepository testStepRepository, ActionTypeService actionTypeService) {
        this.testStepRepository = testStepRepository;
        this.actionTypeService = actionTypeService;
    }

    /**
     * Create a new test step
     */
    public TestStep createTestStep(TestStep testStep) {
        // Ensure sequence order is unique within the scenario
        if (testStepRepository.existsByTestScenarioAndSequenceOrder(testStep.getTestScenario(), testStep.getSequenceOrder())) {
            throw new IllegalArgumentException("A step with sequence order " + testStep.getSequenceOrder() +
                " already exists in this scenario");
        }
        return testStepRepository.save(testStep);
    }

    /**
     * Create multiple test steps for a scenario
     */
    public List<TestStep> createTestSteps(List<TestStep> testSteps) {
        // Validate that sequence orders are unique within each scenario
        testSteps.forEach(step -> {
            if (testStepRepository.existsByTestScenarioAndSequenceOrder(step.getTestScenario(), step.getSequenceOrder())) {
                throw new IllegalArgumentException("A step with sequence order " + step.getSequenceOrder() +
                    " already exists in scenario: " + step.getTestScenario().getName());
            }
        });
        return testStepRepository.saveAll(testSteps);
    }

    /**
     * Get test step by ID
     */
    @Transactional(readOnly = true)
    public TestStep getTestStepById(Long stepId) {
        return testStepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Test step not found with ID: " + stepId));
    }

    /**
     * Get all test steps for a scenario
     */
    @Transactional(readOnly = true)
    public List<TestStep> getTestStepsByScenario(TestScenario testScenario) {
        return testStepRepository.findByTestScenarioOrderBySequenceOrder(testScenario);
    }

    /**
     * Get all test steps for a scenario by scenario ID
     */
    @Transactional(readOnly = true)
    public List<TestStep> getTestStepsByScenarioId(Long scenarioId) {
        return testStepRepository.findByTestScenario_ScenarioIdOrderBySequenceOrder(scenarioId);
    }

    /**
     * Get test steps by action type
     */
    @Transactional(readOnly = true)
    public List<TestStep> getTestStepsByActionType(ActionType actionType) {
        return testStepRepository.findByActionType(actionType);
    }

    /**
     * Get all test steps with pagination
     */
    @Transactional(readOnly = true)
    public Page<TestStep> getTestSteps(Pageable pageable) {
        return testStepRepository.findAll(pageable);
    }

    /**
     * Update test step
     */
    public TestStep updateTestStep(Long stepId, TestStep testStepDetails) {
        TestStep existingStep = getTestStepById(stepId);

        // Check if sequence order is being changed and if new order conflicts
        if (!existingStep.getSequenceOrder().equals(testStepDetails.getSequenceOrder()) &&
            testStepRepository.existsByTestScenarioAndSequenceOrder(existingStep.getTestScenario(), testStepDetails.getSequenceOrder())) {
            throw new IllegalArgumentException("A step with sequence order " + testStepDetails.getSequenceOrder() +
                " already exists in this scenario");
        }

        existingStep.setSequenceOrder(testStepDetails.getSequenceOrder());
        existingStep.setLastStep(testStepDetails.getLastStep());
        existingStep.setActionType(testStepDetails.getActionType());
        existingStep.setSelector(testStepDetails.getSelector());
        existingStep.setInputValue(testStepDetails.getInputValue());
        existingStep.setExpectedOutcome(testStepDetails.getExpectedOutcome());

        return testStepRepository.save(existingStep);
    }

    /**
     * Update sequence order
     */
    public TestStep updateSequenceOrder(Long stepId, Integer newSequenceOrder) {
        TestStep step = getTestStepById(stepId);

        if (testStepRepository.existsByTestScenarioAndSequenceOrder(step.getTestScenario(), newSequenceOrder)) {
            throw new IllegalArgumentException("A step with sequence order " + newSequenceOrder +
                " already exists in this scenario");
        }

        step.setSequenceOrder(newSequenceOrder);
        return testStepRepository.save(step);
    }

    /**
     * Mark step as last step
     */
    public TestStep markAsLastStep(Long stepId, Boolean isLastStep) {
        TestStep step = getTestStepById(stepId);
        step.setLastStep(isLastStep);
        return testStepRepository.save(step);
    }

    /**
     * Delete test step
     */
    public void deleteTestStep(Long stepId) {
        TestStep testStep = getTestStepById(stepId);
        testStepRepository.delete(testStep);
    }

    /**
     * Delete all test steps for a scenario
     */
    public void deleteTestStepsByScenario(TestScenario testScenario) {
        List<TestStep> steps = getTestStepsByScenario(testScenario);
        testStepRepository.deleteAll(steps);
    }

    /**
     * Get count of test steps for a scenario
     */
    @Transactional(readOnly = true)
    public long countTestStepsByScenario(TestScenario testScenario) {
        return testStepRepository.countByTestScenario(testScenario);
    }

    /**
     * Reorder test steps for a scenario
     */
    public List<TestStep> reorderTestSteps(TestScenario testScenario, List<Long> stepIds) {
        List<TestStep> steps = getTestStepsByScenario(testScenario);

        // Validate that all provided IDs belong to the scenario
        for (Long stepId : stepIds) {
            if (steps.stream().noneMatch(step -> step.getStepId().equals(stepId))) {
                throw new IllegalArgumentException("Step with ID " + stepId + " does not belong to this scenario");
            }
        }

        // Update sequence orders
        for (int i = 0; i < stepIds.size(); i++) {
            Long stepId = stepIds.get(i);
            TestStep step = steps.stream()
                    .filter(s -> s.getStepId().equals(stepId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Step not found with ID: " + stepId));

            step.setSequenceOrder(i + 1);
        }

        return testStepRepository.saveAll(steps);
    }

    /**
     * Duplicate test step
     */
    public TestStep duplicateTestStep(Long stepId, TestScenario targetScenario, Integer newSequenceOrder) {
        TestStep originalStep = getTestStepById(stepId);

        if (testStepRepository.existsByTestScenarioAndSequenceOrder(targetScenario, newSequenceOrder)) {
            throw new IllegalArgumentException("A step with sequence order " + newSequenceOrder +
                " already exists in the target scenario");
        }

        TestStep duplicatedStep = new TestStep();
        duplicatedStep.setTestScenario(targetScenario);
        duplicatedStep.setSequenceOrder(newSequenceOrder);
        duplicatedStep.setLastStep(originalStep.getLastStep());
        duplicatedStep.setActionType(originalStep.getActionType());
        duplicatedStep.setSelector(originalStep.getSelector());
        duplicatedStep.setInputValue(originalStep.getInputValue());
        duplicatedStep.setExpectedOutcome(originalStep.getExpectedOutcome());

        return testStepRepository.save(duplicatedStep);
    }

    /**
     * Get next sequence order for a scenario
     */
    @Transactional(readOnly = true)
    public Integer getNextSequenceOrder(TestScenario testScenario) {
        Integer maxOrder = testStepRepository.findMaxSequenceOrderByTestScenario(testScenario);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }

    /**
     * Get last step in a scenario
     */
    @Transactional(readOnly = true)
    public TestStep getLastStepInScenario(TestScenario testScenario) {
        return testStepRepository.findFirstByTestScenarioOrderBySequenceOrderDesc(testScenario)
                .orElse(null);
    }

    /**
     * Update last step markers for a scenario
     */
    public void updateLastStepMarkers(TestScenario testScenario) {
        List<TestStep> steps = getTestStepsByScenario(testScenario);

        // Reset all last step markers
        steps.forEach(step -> step.setLastStep(false));

        // Mark the last step (highest sequence order)
        if (!steps.isEmpty()) {
            TestStep lastStep = steps.get(steps.size() - 1);
            lastStep.setLastStep(true);
        }

        testStepRepository.saveAll(steps);
    }
}
