package com.InSpace.Api.services;

import com.InSpace.Api.domain.TestAction;
import com.InSpace.Api.domain.TestStep;
import com.InSpace.Api.infra.repository.TestActionRepository;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TestActionService {

    private final TestActionRepository testActionRepository;

    @Autowired
    public TestActionService(TestActionRepository testActionRepository) {
        this.testActionRepository = testActionRepository;
    }

    /**
     * Create a new test action
     */
    public TestAction createTestAction(TestAction testAction) {
        return testActionRepository.save(testAction);
    }

    /**
     * Create multiple test actions for a test step
     */
    public List<TestAction> createTestActions(List<TestAction> testActions) {
        return testActionRepository.saveAll(testActions);
    }

    /**
     * Get test action by ID
     */
    @Transactional(readOnly = true)
    public TestAction getTestActionById(Long actionId) {
        return testActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Test action not found with ID: " + actionId));
    }

    /**
     * Get all test actions for a test step
     */
    @Transactional(readOnly = true)
    public List<TestAction> getTestActionsByTestStep(TestStep testStep) {
        return testActionRepository.findByTestStepOrderByExecutionOrder(testStep);
    }

    /**
     * Get all test actions for a test step by step ID
     */
    @Transactional(readOnly = true)
    public List<TestAction> getTestActionsByStepId(Long stepId) {
        return testActionRepository.findByTestStep_StepIdOrderByExecutionOrder(stepId);
    }

    /**
     * Get test actions by action type
     */
    @Transactional(readOnly = true)
    public List<TestAction> getTestActionsByType(String actionType) {
        return testActionRepository.findByActionType(actionType);
    }

    /**
     * Get test actions that require immediate verification
     */
    @Transactional(readOnly = true)
    public List<TestAction> getImmediateVerificationActions(TestStep testStep) {
        return testActionRepository.findImmediateVerificationsByTestStep(testStep);
    }

    /**
     * Get all test actions for a scenario
     */
    @Transactional(readOnly = true)
    public List<TestAction> getTestActionsByScenarioId(Long scenarioId) {
        return testActionRepository.findByScenarioIdOrderByStepAndExecution(scenarioId);
    }

    /**
     * Get test actions that need verification
     */
    @Transactional(readOnly = true)
    public List<TestAction> getVerificationActions(Boolean verifyImmediately) {
        return testActionRepository.findByVerifyImmediately(verifyImmediately);
    }

    /**
     * Get all test actions with pagination
     */
    @Transactional(readOnly = true)
    public Page<TestAction> getTestActions(Pageable pageable) {
        return testActionRepository.findAll(pageable);
    }

    /**
     * Update test action
     */
    public TestAction updateTestAction(Long actionId, TestAction testActionDetails) {
        TestAction existingAction = getTestActionById(actionId);

        existingAction.setActionType(testActionDetails.getActionType());
        existingAction.setDescription(testActionDetails.getDescription());
        existingAction.setExecutionOrder(testActionDetails.getExecutionOrder());
        existingAction.setVerifyImmediately(testActionDetails.getVerifyImmediately());
        existingAction.setVerificationType(testActionDetails.getVerificationType());
        existingAction.setTargetContent(testActionDetails.getTargetContent());
        existingAction.setTargetType(testActionDetails.getTargetType());
        existingAction.setTargetBboxX1(testActionDetails.getTargetBboxX1());
        existingAction.setTargetBboxY1(testActionDetails.getTargetBboxY1());
        existingAction.setTargetBboxX2(testActionDetails.getTargetBboxX2());
        existingAction.setTargetBboxY2(testActionDetails.getTargetBboxY2());
        existingAction.setExpectedContent(testActionDetails.getExpectedContent());
        existingAction.setParamText(testActionDetails.getParamText());
        existingAction.setParamClearFirst(testActionDetails.getParamClearFirst());
        existingAction.setParamDirection(testActionDetails.getParamDirection());
        existingAction.setParamTimeout(testActionDetails.getParamTimeout());
        existingAction.setParamWaitTime(testActionDetails.getParamWaitTime());
        existingAction.setParamUrl(testActionDetails.getParamUrl());
        existingAction.setContextData(testActionDetails.getContextData());

        return testActionRepository.save(existingAction);
    }

    /**
     * Update test action execution order
     */
    public TestAction updateExecutionOrder(Long actionId, Integer newExecutionOrder) {
        TestAction action = getTestActionById(actionId);
        action.setExecutionOrder(newExecutionOrder);
        return testActionRepository.save(action);
    }

    /**
     * Update verification settings
     */
    public TestAction updateVerificationSettings(Long actionId, Boolean verifyImmediately, String verificationType) {
        TestAction action = getTestActionById(actionId);
        action.setVerifyImmediately(verifyImmediately);
        action.setVerificationType(verificationType);
        return testActionRepository.save(action);
    }

    /**
     * Delete test action
     */
    public void deleteTestAction(Long actionId) {
        TestAction testAction = getTestActionById(actionId);
        testActionRepository.delete(testAction);
    }

    /**
     * Delete all test actions for a test step
     */
    public void deleteTestActionsByTestStep(TestStep testStep) {
        List<TestAction> actions = getTestActionsByTestStep(testStep);
        testActionRepository.deleteAll(actions);
    }

    /**
     * Count test actions for a test step
     */
    @Transactional(readOnly = true)
    public long countTestActionsByTestStep(TestStep testStep) {
        return testActionRepository.countByTestStep(testStep);
    }

    /**
     * Reorder test actions for a test step
     */
    public List<TestAction> reorderTestActions(TestStep testStep, List<Long> actionIds) {
        List<TestAction> actions = getTestActionsByTestStep(testStep);

        for (int i = 0; i < actionIds.size(); i++) {
            Long actionId = actionIds.get(i);
            TestAction action = actions.stream()
                    .filter(a -> a.getActionId().equals(actionId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Test action not found with ID: " + actionId));

            action.setExecutionOrder(i + 1);
        }

        return testActionRepository.saveAll(actions);
    }

    /**
     * Duplicate test action
     */
    public TestAction duplicateTestAction(Long actionId, TestStep targetTestStep) {
        TestAction originalAction = getTestActionById(actionId);

        TestAction duplicatedAction = new TestAction();
        duplicatedAction.setTestStep(targetTestStep);
        duplicatedAction.setActionType(originalAction.getActionType());
        duplicatedAction.setDescription(originalAction.getDescription());
        duplicatedAction.setExecutionOrder(originalAction.getExecutionOrder());
        duplicatedAction.setVerifyImmediately(originalAction.getVerifyImmediately());
        duplicatedAction.setVerificationType(originalAction.getVerificationType());
        duplicatedAction.setTargetContent(originalAction.getTargetContent());
        duplicatedAction.setTargetType(originalAction.getTargetType());
        duplicatedAction.setTargetBboxX1(originalAction.getTargetBboxX1());
        duplicatedAction.setTargetBboxY1(originalAction.getTargetBboxY1());
        duplicatedAction.setTargetBboxX2(originalAction.getTargetBboxX2());
        duplicatedAction.setTargetBboxY2(originalAction.getTargetBboxY2());
        duplicatedAction.setExpectedContent(originalAction.getExpectedContent());
        duplicatedAction.setParamText(originalAction.getParamText());
        duplicatedAction.setParamClearFirst(originalAction.getParamClearFirst());
        duplicatedAction.setParamDirection(originalAction.getParamDirection());
        duplicatedAction.setParamTimeout(originalAction.getParamTimeout());
        duplicatedAction.setParamWaitTime(originalAction.getParamWaitTime());
        duplicatedAction.setParamUrl(originalAction.getParamUrl());
        duplicatedAction.setContextData(originalAction.getContextData());

        return testActionRepository.save(duplicatedAction);
    }
}
