package com.InSpace.Api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "execution_status")
public class ExecutionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    @Column(name = "name", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Status name is required")
    @Size(max = 20, message = "Status name cannot exceed 20 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public ExecutionStatus() {
    }

    public ExecutionStatus(String name) {
        this.name = name;
    }

    public ExecutionStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
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

    @Override
    public String toString() {
        return "ExecutionStatus{" +
                "statusId=" + statusId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionStatus that = (ExecutionStatus) o;
        return statusId != null && statusId.equals(that.statusId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
