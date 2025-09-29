package com.InSpace.Api.services;

import com.InSpace.Api.domain.ExecutionStatus;
import com.InSpace.Api.domain.TestExecution;
import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestStep;
import com.InSpace.Api.infra.repository.TestExecutionRepository;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TestExecutionService {

    private final TestExecutionRepository testExecutionRepository;
    private final ExecutionStatusService executionStatusService;

    @Autowired
    public TestExecutionService(TestExecutionRepository testExecutionRepository,
                               ExecutionStatusService executionStatusService) {
        this.testExecutionRepository = testExecutionRepository;
        this.executionStatusService = executionStatusService;
    }

    /**
     * Create a new test execution
     */
    public TestExecution createTestExecution(TestExecution testExecution) {
        // Set default status if not provided
        if (testExecution.getExecutionStatus() == null) {
            ExecutionStatus defaultStatus = executionStatusService.getExecutionStatusByName("pending");
            testExecution.setExecutionStatus(defaultStatus);
        }

        return testExecutionRepository.save(testExecution);
    }

    /**
     * Start a test execution run for a scenario
     */
    public List<TestExecution> startTestExecutionRun(TestScenario testScenario, List<TestStep> testSteps) {
        UUID runIdentifier = UUID.randomUUID();
        ExecutionStatus pendingStatus = executionStatusService.getExecutionStatusByName("pending");

        List<TestExecution> executions = testSteps.stream()
                .map(step -> {
                    TestExecution execution = new TestExecution(testScenario, step, runIdentifier, pendingStatus);
                    return testExecutionRepository.save(execution);
                })
                .toList();

        return executions;
    }

    /**
     * Get test execution by ID
     */
    @Transactional(readOnly = true)
    public TestExecution getTestExecutionById(Long executionId) {
        return testExecutionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("Test execution not found with ID: " + executionId));
    }

    /**
     * Get all test executions for a run identifier
     */
    @Transactional(readOnly = true)
    public List<TestExecution> getTestExecutionsByRunIdentifier(UUID runIdentifier) {
        return testExecutionRepository.findByRunIdentifierOrderByTestStep_SequenceOrder(runIdentifier);
    }

    /**
     * Get test executions by scenario ID
     */
    @Transactional(readOnly = true)
    public List<TestExecution> getTestExecutionsByScenarioId(Long scenarioId) {
        return testExecutionRepository.findByTestScenario_ScenarioIdOrderByStartedAtDesc(scenarioId);
    }

    /**
     * Get test executions by status
     */
    @Transactional(readOnly = true)
    public List<TestExecution> getTestExecutionsByStatus(String statusName) {
        return testExecutionRepository.findByExecutionStatus_Name(statusName);
    }

    /**
     * Get all test executions with pagination
     */
    @Transactional(readOnly = true)
    public Page<TestExecution> getTestExecutions(Pageable pageable) {
        return testExecutionRepository.findAll(pageable);
    }

    /**
     * Update test execution status
     */
    public TestExecution updateExecutionStatus(Long executionId, String statusName) {
        TestExecution execution = getTestExecutionById(executionId);
        ExecutionStatus newStatus = executionStatusService.getExecutionStatusByName(statusName);

        execution.setExecutionStatus(newStatus);

        // Set finished time if status is final
        if (isFinalStatus(statusName)) {
            execution.setFinishedAt(LocalDateTime.now());
        }

        return testExecutionRepository.save(execution);
    }

    /**
     * Update test execution with results
     */
    public TestExecution updateTestExecutionResults(Long executionId, String actualOutput,
                                                   String screenshotUrl, String statusName) {
        TestExecution execution = getTestExecutionById(executionId);
        ExecutionStatus status = executionStatusService.getExecutionStatusByName(statusName);

        execution.setActualOutput(actualOutput);
        execution.setScreenshotUrl(screenshotUrl);
        execution.setExecutionStatus(status);

        if (isFinalStatus(statusName)) {
            execution.setFinishedAt(LocalDateTime.now());
        }

        return testExecutionRepository.save(execution);
    }

    /**
     * Get distinct run identifiers for a scenario
     */
    @Transactional(readOnly = true)
    public List<UUID> getRunIdentifiersByScenario(TestScenario scenario) {
        return testExecutionRepository.findDistinctRunIdentifiersByTestScenarioOrderByStartedAtDesc(scenario);
    }

    /**
     * Get execution statistics for a run
     */
    @Transactional(readOnly = true)
    public ExecutionStats getExecutionStats(UUID runIdentifier) {
        long total = testExecutionRepository.countByRunIdentifier(runIdentifier);
        long passed = testExecutionRepository.countPassedByRunIdentifier(runIdentifier);
        long failed = testExecutionRepository.countFailedByRunIdentifier(runIdentifier);

        return new ExecutionStats(total, passed, failed);
    }

    /**
     * Get executions by run identifier and status
     */
    @Transactional(readOnly = true)
    public List<TestExecution> getExecutionsByRunAndStatus(UUID runIdentifier, String statusName) {
        return testExecutionRepository.findByRunIdentifierAndStatusName(runIdentifier, statusName);
    }

    /**
     * Delete test execution
     */
    public void deleteTestExecution(Long executionId) {
        TestExecution execution = getTestExecutionById(executionId);
        testExecutionRepository.delete(execution);
    }

    /**
     * Delete all executions for a run
     */
    public void deleteExecutionsByRunIdentifier(UUID runIdentifier) {
        List<TestExecution> executions = getTestExecutionsByRunIdentifier(runIdentifier);
        testExecutionRepository.deleteAll(executions);
    }

    private boolean isFinalStatus(String statusName) {
        return "passed".equals(statusName) || "failed".equals(statusName) ||
               "error".equals(statusName) || "skipped".equals(statusName);
    }

    /**
     * Execution statistics DTO
     */
    public static class ExecutionStats {
        private final long total;
        private final long passed;
        private final long failed;
        private final long pending;

        public ExecutionStats(long total, long passed, long failed) {
            this.total = total;
            this.passed = passed;
            this.failed = failed;
            this.pending = total - passed - failed;
        }

        public long getTotal() { return total; }
        public long getPassed() { return passed; }
        public long getFailed() { return failed; }
        public long getPending() { return pending; }
        public double getPassRate() {
            return total > 0 ? (double) passed / total * 100 : 0;
        }
    }
}
