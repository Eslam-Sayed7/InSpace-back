package com.InSpace.Api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_verifications")
public class BatchVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    @NotNull(message = "Test action is required")
    private TestAction testAction;

    @Column(name = "criteria_type", nullable = false, length = 50)
    @NotBlank(message = "Criteria type is required")
    @Size(max = 50, message = "Criteria type cannot exceed 50 characters")
    private String criteriaType;

    @Column(name = "criteria_content", nullable = false, length = 500)
    @NotBlank(message = "Criteria content is required")
    @Size(max = 500, message = "Criteria content cannot exceed 500 characters")
    private String criteriaContent;

    @Column(name = "verification_status", length = 20)
    @Size(max = 20, message = "Verification status cannot exceed 20 characters")
    private String verificationStatus = "pending";

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BatchVerification() {
    }

    public BatchVerification(TestAction testAction, String criteriaType, String criteriaContent) {
        this.testAction = testAction;
        this.criteriaType = criteriaType;
        this.criteriaContent = criteriaContent;
    }

    // Getters and Setters
    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public TestAction getTestAction() {
        return testAction;
    }

    public void setTestAction(TestAction testAction) {
        this.testAction = testAction;
    }

    public String getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    public String getCriteriaContent() {
        return criteriaContent;
    }

    public void setCriteriaContent(String criteriaContent) {
        this.criteriaContent = criteriaContent;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BatchVerification{" +
                "batchId=" + batchId +
                ", criteriaType='" + criteriaType + '\'' +
                ", criteriaContent='" + criteriaContent + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", verifiedAt=" + verifiedAt +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchVerification that = (BatchVerification) o;
        return batchId != null && batchId.equals(that.batchId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
