package com.InSpace.Api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_steps",
       uniqueConstraints = @UniqueConstraint(columnNames = {"scenario_id", "sequence_order"}))
public class TestStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @NotNull(message = "Test scenario is required")
    private TestScenario testScenario;

    @Column(name = "sequence_order", nullable = false)
    @NotNull(message = "Sequence order is required")
    private Integer sequenceOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id", nullable = false)
    @NotNull(message = "Action type is required")
    private ActionType actionType;

    @Column(name = "selector", columnDefinition = "TEXT")
    private String selector;

    @Column(name = "input_value", columnDefinition = "TEXT")
    private String inputValue;

    @Column(name = "expected_outcome", columnDefinition = "TEXT")
    private String expectedOutcome;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TestStep() {
    }

    public TestStep(TestScenario testScenario, Integer sequenceOrder, ActionType actionType) {
        this.testScenario = testScenario;
        this.sequenceOrder = sequenceOrder;
        this.actionType = actionType;
    }

    public TestStep(TestScenario testScenario, Integer sequenceOrder, ActionType actionType, String selector) {
        this.testScenario = testScenario;
        this.sequenceOrder = sequenceOrder;
        this.actionType = actionType;
        this.selector = selector;
    }

    // Getters and Setters
    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public TestScenario getTestScenario() {
        return testScenario;
    }

    public void setTestScenario(TestScenario testScenario) {
        this.testScenario = testScenario;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public String getExpectedOutcome() {
        return expectedOutcome;
    }

    public void setExpectedOutcome(String expectedOutcome) {
        this.expectedOutcome = expectedOutcome;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TestStep{" +
                "stepId=" + stepId +
                ", sequenceOrder=" + sequenceOrder +
                ", selector='" + selector + '\'' +
                ", inputValue='" + inputValue + '\'' +
                ", expectedOutcome='" + expectedOutcome + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestStep testStep = (TestStep) o;
        return stepId != null && stepId.equals(testStep.stepId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
