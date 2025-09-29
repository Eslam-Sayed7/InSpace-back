package com.InSpace.Api.services;

import com.InSpace.Api.domain.BatchVerification;
import com.InSpace.Api.domain.TestAction;
import com.InSpace.Api.infra.repository.BatchVerificationRepository;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BatchVerificationService {

    private final BatchVerificationRepository batchVerificationRepository;

    @Autowired
    public BatchVerificationService(BatchVerificationRepository batchVerificationRepository) {
        this.batchVerificationRepository = batchVerificationRepository;
    }

    /**
     * Create a new batch verification
     */
    public BatchVerification createBatchVerification(BatchVerification batchVerification) {
        return batchVerificationRepository.save(batchVerification);
    }

    /**
     * Create multiple batch verifications
     */
    public List<BatchVerification> createBatchVerifications(List<BatchVerification> batchVerifications) {
        return batchVerificationRepository.saveAll(batchVerifications);
    }

    /**
     * Get batch verification by ID
     */
    @Transactional(readOnly = true)
    public BatchVerification getBatchVerificationById(Long batchId) {
        return batchVerificationRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch verification not found with ID: " + batchId));
    }

    /**
     * Get all batch verifications for a test action
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getBatchVerificationsByTestAction(TestAction testAction) {
        return batchVerificationRepository.findByTestAction(testAction);
    }

    /**
     * Get all batch verifications for a test action by action ID
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getBatchVerificationsByActionId(Long actionId) {
        return batchVerificationRepository.findByTestAction_ActionId(actionId);
    }

    /**
     * Get batch verifications by verification status
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getBatchVerificationsByStatus(String verificationStatus) {
        return batchVerificationRepository.findByVerificationStatus(verificationStatus);
    }

    /**
     * Get batch verifications by criteria type
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getBatchVerificationsByCriteriaType(String criteriaType) {
        return batchVerificationRepository.findByCriteriaType(criteriaType);
    }

    /**
     * Get all batch verifications for a scenario
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getBatchVerificationsByScenarioId(Long scenarioId) {
        return batchVerificationRepository.findByScenarioIdOrderByStepAndAction(scenarioId);
    }

    /**
     * Get all batch verifications for a test step
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getBatchVerificationsByStepId(Long stepId) {
        return batchVerificationRepository.findByStepIdOrderByActionExecution(stepId);
    }

    /**
     * Get pending batch verifications
     */
    @Transactional(readOnly = true)
    public List<BatchVerification> getPendingBatchVerifications() {
        return batchVerificationRepository.findPendingVerificationsOrderByCreatedAt();
    }

    /**
     * Get all batch verifications with pagination
     */
    @Transactional(readOnly = true)
    public Page<BatchVerification> getBatchVerifications(Pageable pageable) {
        return batchVerificationRepository.findAll(pageable);
    }

    /**
     * Update batch verification
     */
    public BatchVerification updateBatchVerification(Long batchId, BatchVerification batchVerificationDetails) {
        BatchVerification existingVerification = getBatchVerificationById(batchId);

        existingVerification.setCriteriaType(batchVerificationDetails.getCriteriaType());
        existingVerification.setCriteriaContent(batchVerificationDetails.getCriteriaContent());
        existingVerification.setVerificationStatus(batchVerificationDetails.getVerificationStatus());
        existingVerification.setVerifiedAt(batchVerificationDetails.getVerifiedAt());

        return batchVerificationRepository.save(existingVerification);
    }

    /**
     * Update verification status
     */
    public BatchVerification updateVerificationStatus(Long batchId, String verificationStatus) {
        BatchVerification verification = getBatchVerificationById(batchId);
        verification.setVerificationStatus(verificationStatus);

        // Set verified time if status is final
        if (isFinalStatus(verificationStatus)) {
            verification.setVerifiedAt(LocalDateTime.now());
        }

        return batchVerificationRepository.save(verification);
    }

    /**
     * Mark verification as passed
     */
    public BatchVerification markAsPassed(Long batchId) {
        return updateVerificationStatus(batchId, "passed");
    }

    /**
     * Mark verification as failed
     */
    public BatchVerification markAsFailed(Long batchId) {
        return updateVerificationStatus(batchId, "failed");
    }

    /**
     * Mark verification as pending
     */
    public BatchVerification markAsPending(Long batchId) {
        return updateVerificationStatus(batchId, "pending");
    }

    /**
     * Process pending verifications for a test action
     */
    public List<BatchVerification> processPendingVerifications(TestAction testAction) {
        List<BatchVerification> pendingVerifications = batchVerificationRepository.findByTestAction(testAction)
                .stream()
                .filter(bv -> "pending".equals(bv.getVerificationStatus()))
                .toList();

        // This is where you would implement actual verification logic
        // For now, we'll just return the pending verifications
        return pendingVerifications;
    }

    /**
     * Get verification statistics for a test action
     */
    @Transactional(readOnly = true)
    public VerificationStats getVerificationStats(TestAction testAction) {
        long passed = batchVerificationRepository.countByTestActionAndStatus(testAction, "passed");
        long failed = batchVerificationRepository.countByTestActionAndStatus(testAction, "failed");
        long pending = batchVerificationRepository.countByTestActionAndStatus(testAction, "pending");

        return new VerificationStats(passed, failed, pending);
    }

    /**
     * Delete batch verification
     */
    public void deleteBatchVerification(Long batchId) {
        BatchVerification batchVerification = getBatchVerificationById(batchId);
        batchVerificationRepository.delete(batchVerification);
    }

    /**
     * Delete all batch verifications for a test action
     */
    public void deleteBatchVerificationsByTestAction(TestAction testAction) {
        List<BatchVerification> verifications = getBatchVerificationsByTestAction(testAction);
        batchVerificationRepository.deleteAll(verifications);
    }

    /**
     * Bulk update verification statuses
     */
    public List<BatchVerification> bulkUpdateVerificationStatus(List<Long> batchIds, String verificationStatus) {
        List<BatchVerification> verifications = batchVerificationRepository.findAllById(batchIds);

        LocalDateTime verifiedAt = isFinalStatus(verificationStatus) ? LocalDateTime.now() : null;

        verifications.forEach(verification -> {
            verification.setVerificationStatus(verificationStatus);
            verification.setVerifiedAt(verifiedAt);
        });

        return batchVerificationRepository.saveAll(verifications);
    }

    /**
     * Create verification from test action
     */
    public BatchVerification createVerificationFromAction(TestAction testAction, String criteriaType, String criteriaContent) {
        BatchVerification verification = new BatchVerification(testAction, criteriaType, criteriaContent);
        return batchVerificationRepository.save(verification);
    }

    private boolean isFinalStatus(String verificationStatus) {
        return "passed".equals(verificationStatus) || "failed".equals(verificationStatus);
    }

    /**
     * Verification statistics DTO
     */
    public static class VerificationStats {
        private final long passed;
        private final long failed;
        private final long pending;
        private final long total;

        public VerificationStats(long passed, long failed, long pending) {
            this.passed = passed;
            this.failed = failed;
            this.pending = pending;
            this.total = passed + failed + pending;
        }

        public long getPassed() { return passed; }
        public long getFailed() { return failed; }
        public long getPending() { return pending; }
        public long getTotal() { return total; }
        public double getPassRate() {
            return total > 0 ? (double) passed / total * 100 : 0;
        }
        public double getFailRate() {
            return total > 0 ? (double) failed / total * 100 : 0;
        }
    }
}
