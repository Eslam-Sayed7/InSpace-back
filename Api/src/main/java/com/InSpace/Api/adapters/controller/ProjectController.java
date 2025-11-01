package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.Project;
import com.InSpace.Api.services.ProjectService;
import com.InSpace.Api.services.dto.ErrorResponse;
import com.InSpace.Api.services.dto.SuccessResponse;
import com.InSpace.Api.services.dto.Project.CreateProjectRequest;
import com.InSpace.Api.services.dto.Project.UpdateProjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request) {
        try {
            Project project;
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                project = projectService.createProject(request.getName(), request.getDescription());
            } else {
                project = projectService.createProject(request.getName());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.findById(id);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Project with ID " + id + " not found"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getProjectByName(@RequestParam String name) {
        Optional<Project> project = projectService.findByNameIgnoreCase(name);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Project with name '" + name + "' not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id,
            @RequestBody @Valid UpdateProjectRequest request) {
        try {
            Project updatedProject = projectService.update(id, request.getName(), request.getDescription());
            return ResponseEntity.ok(updatedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteById(id);
            return ResponseEntity.ok(new SuccessResponse("Project deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkProjectExists(@RequestParam String name) {
        boolean exists = projectService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}
