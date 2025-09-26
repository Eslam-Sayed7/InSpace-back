package com.InSpace.Api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "action_types")
public class ActionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_type_id")
    private Long actionTypeId;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Action type name is required")
    @Size(max = 50, message = "Action type name cannot exceed 50 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public ActionType() {
    }

    public ActionType(String name) {
        this.name = name;
    }

    public ActionType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(Long actionTypeId) {
        this.actionTypeId = actionTypeId;
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
        return "ActionType{" +
                "actionTypeId=" + actionTypeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionType that = (ActionType) o;
        return actionTypeId != null && actionTypeId.equals(that.actionTypeId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
