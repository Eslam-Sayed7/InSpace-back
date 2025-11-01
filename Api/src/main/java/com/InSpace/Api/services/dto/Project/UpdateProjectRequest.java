package com.InSpace.Api.services.dto.Project;

import jakarta.validation.constraints.NotBlank;

public class UpdateProjectRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;

    public UpdateProjectRequest() {
    }

    public UpdateProjectRequest(String name, String description) {
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
}
