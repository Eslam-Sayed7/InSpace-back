package com.InSpace.Api.services.dto.TestSuite;

import jakarta.validation.constraints.NotBlank;

// DTOs for request/response
public class CreateTestSuiteRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;

    // Constructors
    public CreateTestSuiteRequest() {
    }

    public CreateTestSuiteRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
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