package com.InSpace.Api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_actions")
public class TestAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    @NotNull(message = "Test step is required")
    private TestStep testStep;

    @Column(name = "action_type", nullable = false, length = 50)
    @NotBlank(message = "Action type is required")
    private String actionType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "execution_order")
    private Integer executionOrder;

    @Column(name = "verify_immediately")
    private Boolean verifyImmediately = false;

    @Column(name = "verification_type", length = 50)
    private String verificationType;

    @Column(name = "target_content", columnDefinition = "TEXT")
    private String targetContent;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_bbox_x1", columnDefinition = "TEXT")
    private String targetBboxX1;

    @Column(name = "target_bbox_y1", columnDefinition = "TEXT")
    private String targetBboxY1;

    @Column(name = "target_bbox_x2", columnDefinition = "TEXT")
    private String targetBboxX2;

    @Column(name = "target_bbox_y2", columnDefinition = "TEXT")
    private String targetBboxY2;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "expected_content", columnDefinition = "jsonb")
    private JsonNode expectedContent;

    @Column(name = "param_text", columnDefinition = "TEXT")
    private String paramText;

    @Column(name = "param_clear_first")
    private Boolean paramClearFirst;

    @Column(name = "param_direction", length = 20)
    private String paramDirection;

    @Column(name = "param_timeout")
    private Integer paramTimeout;

    @Column(name = "param_wait_time")
    private Integer paramWaitTime;

    @Column(name = "param_url", length = 1000)
    private String paramUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_data", columnDefinition = "jsonb")
    private JsonNode contextData;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TestAction() {
    }

    public TestAction(TestStep testStep, String actionType) {
        this.testStep = testStep;
        this.actionType = actionType;
    }

    // Getters and Setters
    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public TestStep getTestStep() {
        return testStep;
    }

    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Integer executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Boolean getVerifyImmediately() {
        return verifyImmediately;
    }

    public void setVerifyImmediately(Boolean verifyImmediately) {
        this.verifyImmediately = verifyImmediately;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getTargetContent() {
        return targetContent;
    }

    public void setTargetContent(String targetContent) {
        this.targetContent = targetContent;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetBboxX1() {
        return targetBboxX1;
    }

    public void setTargetBboxX1(String targetBboxX1) {
        this.targetBboxX1 = targetBboxX1;
    }

    public String getTargetBboxY1() {
        return targetBboxY1;
    }

    public void setTargetBboxY1(String targetBboxY1) {
        this.targetBboxY1 = targetBboxY1;
    }

    public String getTargetBboxX2() {
        return targetBboxX2;
    }

    public void setTargetBboxX2(String targetBboxX2) {
        this.targetBboxX2 = targetBboxX2;
    }

    public String getTargetBboxY2() {
        return targetBboxY2;
    }

    public void setTargetBboxY2(String targetBboxY2) {
        this.targetBboxY2 = targetBboxY2;
    }

    public JsonNode getExpectedContent() {
        return expectedContent;
    }

    public void setExpectedContent(JsonNode expectedContent) {
        this.expectedContent = expectedContent;
    }

    public String getParamText() {
        return paramText;
    }

    public void setParamText(String paramText) {
        this.paramText = paramText;
    }

    public Boolean getParamClearFirst() {
        return paramClearFirst;
    }

    public void setParamClearFirst(Boolean paramClearFirst) {
        this.paramClearFirst = paramClearFirst;
    }

    public String getParamDirection() {
        return paramDirection;
    }

    public void setParamDirection(String paramDirection) {
        this.paramDirection = paramDirection;
    }

    public Integer getParamTimeout() {
        return paramTimeout;
    }

    public void setParamTimeout(Integer paramTimeout) {
        this.paramTimeout = paramTimeout;
    }

    public Integer getParamWaitTime() {
        return paramWaitTime;
    }

    public void setParamWaitTime(Integer paramWaitTime) {
        this.paramWaitTime = paramWaitTime;
    }

    public String getParamUrl() {
        return paramUrl;
    }

    public void setParamUrl(String paramUrl) {
        this.paramUrl = paramUrl;
    }

    public JsonNode getContextData() {
        return contextData;
    }

    public void setContextData(JsonNode contextData) {
        this.contextData = contextData;
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
        return "TestAction{" +
                "actionId=" + actionId +
                ", actionType='" + actionType + '\'' +
                ", description='" + description + '\'' +
                ", executionOrder=" + executionOrder +
                ", verifyImmediately=" + verifyImmediately +
                ", targetContent='" + targetContent + '\'' +
                ", targetType='" + targetType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAction that = (TestAction) o;
        return actionId != null && actionId.equals(that.actionId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
