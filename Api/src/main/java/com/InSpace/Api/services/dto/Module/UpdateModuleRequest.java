package com.InSpace.Api.services.dto.Module;

import jakarta.validation.constraints.NotBlank;

public class UpdateModuleRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private String scenarios;
    private String acceptanceCriteria;
    private String prerequisite;

    public UpdateModuleRequest() {
    }

    public UpdateModuleRequest(String name, String description) {
        this.name = name;
        this.description = description;
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

    public String getScenarios() {
        return scenarios;
    }

    public void setScenarios(String scenarios) {
        this.scenarios = scenarios;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }
}
