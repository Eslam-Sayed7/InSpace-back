package com.InSpace.Api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import com.InSpace.Api.domain.TestCase;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "test_executions")
public class TestExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long executionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_id", nullable = false)
    @NotNull(message = "Testcase is required")
    private TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    @NotNull(message = "Test step is required")
    private TestStep testStep;

    @Column(name = "run_identifier", nullable = false)
    @NotNull(message = "Run identifier is required")
    private UUID runIdentifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    @NotNull(message = "Execution status is required")
    private ExecutionStatus executionStatus;

    @Column(name = "actual_output", columnDefinition = "TEXT")
    private String actualOutput;

    @Column(name = "screenshot_url", columnDefinition = "TEXT")
    private String screenshotUrl;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public TestExecution() {
    }

    public TestExecution(TestCase testCase, TestStep testStep, UUID runIdentifier, ExecutionStatus executionStatus) {
        this.testCase = testCase;
        this.testStep = testStep;
        this.runIdentifier = runIdentifier;
        this.executionStatus = executionStatus;
    }

    // Getters and Setters
    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public TestStep getTestStep() {
        return testStep;
    }

    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public UUID getRunIdentifier() {
        return runIdentifier;
    }

    public void setRunIdentifier(UUID runIdentifier) {
        this.runIdentifier = runIdentifier;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Override
    public String toString() {
        return "TestExecution{" +
                "executionId=" + executionId +
                ", runIdentifier=" + runIdentifier +
                ", actualOutput='" + actualOutput + '\'' +
                ", screenshotUrl='" + screenshotUrl + '\'' +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestExecution that = (TestExecution) o;
        return executionId != null && executionId.equals(that.executionId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
