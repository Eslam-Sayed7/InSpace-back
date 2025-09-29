package com.InSpace.Api.services;

import com.InSpace.Api.domain.ActionType;
import com.InSpace.Api.infra.repository.ActionTypeRepository;
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
public class ActionTypeService {

    private final ActionTypeRepository actionTypeRepository;

    @Autowired
    public ActionTypeService(ActionTypeRepository actionTypeRepository) {
        this.actionTypeRepository = actionTypeRepository;
    }

    /**
     * Create a new action type
     */
    public ActionType createActionType(ActionType actionType) {
        if (actionTypeRepository.existsByName(actionType.getName())) {
            throw new IllegalArgumentException("Action type with name '" + actionType.getName() + "' already exists");
        }
        return actionTypeRepository.save(actionType);
    }

    /**
     * Get action type by ID
     */
    @Transactional(readOnly = true)
    public ActionType getActionTypeById(Long actionTypeId) {
        return actionTypeRepository.findById(actionTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Action type not found with ID: " + actionTypeId));
    }

    /**
     * Get action type by name
     */
    @Transactional(readOnly = true)
    public ActionType getActionTypeByName(String name) {
        return actionTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Action type not found with name: " + name));
    }

    /**
     * Get all action types
     */
    @Transactional(readOnly = true)
    public List<ActionType> getAllActionTypes() {
        return actionTypeRepository.findAll();
    }

    /**
     * Get action types with pagination
     */
    @Transactional(readOnly = true)
    public Page<ActionType> getActionTypes(Pageable pageable) {
        return actionTypeRepository.findAll(pageable);
    }

    /**
     * Update action type
     */
    public ActionType updateActionType(Long actionTypeId, ActionType actionTypeDetails) {
        ActionType existingActionType = getActionTypeById(actionTypeId);

        // Check if name is being changed and if new name already exists
        if (!existingActionType.getName().equals(actionTypeDetails.getName()) &&
            actionTypeRepository.existsByName(actionTypeDetails.getName())) {
            throw new IllegalArgumentException("Action type with name '" + actionTypeDetails.getName() + "' already exists");
        }

        existingActionType.setName(actionTypeDetails.getName());
        existingActionType.setDescription(actionTypeDetails.getDescription());

        return actionTypeRepository.save(existingActionType);
    }

    /**
     * Delete action type
     */
    public void deleteActionType(Long actionTypeId) {
        ActionType actionType = getActionTypeById(actionTypeId);
        actionTypeRepository.delete(actionType);
    }

    /**
     * Check if action type exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return actionTypeRepository.existsByName(name);
    }

    /**
     * Find action type by name (optional)
     */
    @Transactional(readOnly = true)
    public Optional<ActionType> findByName(String name) {
        return actionTypeRepository.findByName(name);
    }

    /**
     * Initialize default action types
     */
    public void initializeDefaultActionTypes() {
        String[] defaultTypes = {"click", "input_text", "assert_text", "wait", "screenshot", "navigate", "scroll"};
        String[] descriptions = {
            "Click on an element",
            "Input text into a field",
            "Assert text is present",
            "Wait for a condition",
            "Take a screenshot",
            "Navigate to a URL",
            "Scroll the page"
        };

        for (int i = 0; i < defaultTypes.length; i++) {
            if (!existsByName(defaultTypes[i])) {
                ActionType actionType = new ActionType(defaultTypes[i], descriptions[i]);
                actionTypeRepository.save(actionType);
            }
        }
    }

    /**
     * Search action types by name pattern
     */
    @Transactional(readOnly = true)
    public List<ActionType> searchActionTypesByName(String namePattern) {
        return actionTypeRepository.findByNameContainingIgnoreCase(namePattern);
    }
}
