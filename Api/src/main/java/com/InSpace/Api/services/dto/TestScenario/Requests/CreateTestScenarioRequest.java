package com.InSpace.Api.services.dto.TestScenario.Requests;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTestScenarioRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private Short priority;
    @NotNull(message = "Suite ID is required")
    private Long suiteId;
    private Set<String> tagNames;

    public CreateTestScenarioRequest() {
    }

    public CreateTestScenarioRequest(String name, String description,
            Set<String> tagNames, Short priority, Long suiteId) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.suiteId = suiteId;
        this.tagNames = tagNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    public Long getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(Long suiteId) {
        this.suiteId = suiteId;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }
}