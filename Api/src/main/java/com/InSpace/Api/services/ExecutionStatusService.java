package com.InSpace.Api.services;

import com.InSpace.Api.domain.ExecutionStatus;
import com.InSpace.Api.infra.repository.ExecutionStatusRepository;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExecutionStatusService {

    private final ExecutionStatusRepository executionStatusRepository;

    @Autowired
    public ExecutionStatusService(ExecutionStatusRepository executionStatusRepository) {
        this.executionStatusRepository = executionStatusRepository;
    }

    /**
     * Create a new execution status
     */
    public ExecutionStatus createExecutionStatus(ExecutionStatus executionStatus) {
        if (executionStatusRepository.existsByName(executionStatus.getName())) {
            throw new IllegalArgumentException("Execution status with name '" + executionStatus.getName() + "' already exists");
        }
        return executionStatusRepository.save(executionStatus);
    }

    /**
     * Get execution status by ID
     */
    @Transactional(readOnly = true)
    public ExecutionStatus getExecutionStatusById(Long statusId) {
        return executionStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Execution status not found with ID: " + statusId));
    }

    /**
     * Get execution status by name
     */
    @Transactional(readOnly = true)
    public ExecutionStatus getExecutionStatusByName(String name) {
        return executionStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Execution status not found with name: " + name));
    }

    /**
     * Get all execution statuses
     */
    @Transactional(readOnly = true)
    public List<ExecutionStatus> getAllExecutionStatuses() {
        return executionStatusRepository.findAll();
    }

    /**
     * Get execution statuses with pagination
     */
    @Transactional(readOnly = true)
    public Page<ExecutionStatus> getExecutionStatuses(Pageable pageable) {
        return executionStatusRepository.findAll(pageable);
    }

    /**
     * Update execution status
     */
    public ExecutionStatus updateExecutionStatus(Long statusId, ExecutionStatus executionStatusDetails) {
        ExecutionStatus existingStatus = getExecutionStatusById(statusId);

        // Check if name is being changed and if new name already exists
        if (!existingStatus.getName().equals(executionStatusDetails.getName()) &&
            executionStatusRepository.existsByName(executionStatusDetails.getName())) {
            throw new IllegalArgumentException("Execution status with name '" + executionStatusDetails.getName() + "' already exists");
        }

        existingStatus.setName(executionStatusDetails.getName());
        existingStatus.setDescription(executionStatusDetails.getDescription());

        return executionStatusRepository.save(existingStatus);
    }

    /**
     * Delete execution status
     */
    public void deleteExecutionStatus(Long statusId) {
        ExecutionStatus executionStatus = getExecutionStatusById(statusId);
        executionStatusRepository.delete(executionStatus);
    }

    /**
     * Check if execution status exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return executionStatusRepository.existsByName(name);
    }

    /**
     * Find execution status by name (optional)
     */
    @Transactional(readOnly = true)
    public Optional<ExecutionStatus> findByName(String name) {
        return executionStatusRepository.findByName(name);
    }

    /**
     * Get or create default execution statuses
     */
    public void initializeDefaultStatuses() {
        String[] defaultStatuses = {"pending", "running", "passed", "failed", "error", "skipped"};
        String[] descriptions = {
            "Execution has not started yet",
            "Execution is currently in progress",
            "Step executed successfully",
            "Step failed to meet the expected outcome",
            "An unexpected error occurred",
            "Step was skipped in this run"
        };

        for (int i = 0; i < defaultStatuses.length; i++) {
            if (!existsByName(defaultStatuses[i])) {
                ExecutionStatus status = new ExecutionStatus(defaultStatuses[i], descriptions[i]);
                executionStatusRepository.save(status);
            }
        }
    }
}
