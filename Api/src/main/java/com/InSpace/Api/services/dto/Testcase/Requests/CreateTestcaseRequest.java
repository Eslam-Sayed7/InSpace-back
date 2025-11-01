package com.InSpace.Api.services.dto.Testcase.Requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTestcaseRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private Short priority;
    @NotNull(message = "Module ID is required")
    private Long moduleId;

    public CreateTestcaseRequest() {
    }

    public CreateTestcaseRequest(String name, String description,
         Short priority, Long moduleId) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.moduleId = moduleId;
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

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

}
