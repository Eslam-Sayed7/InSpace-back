package com.InSpace.Api.services;

import com.InSpace.Api.domain.Project;
import com.InSpace.Api.infra.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Project createProject(String name) {
        if (projectRepository.existsByName(name)) {
            throw new IllegalArgumentException("Project with name '" + name + "' already exists");
        }
        Project project = new Project(name);
        return projectRepository.save(project);
    }

    public Project createProject(String name, String description) {
        if (projectRepository.existsByName(name)) {
            throw new IllegalArgumentException("Project with name '" + name + "' already exists");
        }
        Project project = new Project(name, description);
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Project> findByNameIgnoreCase(String name) {
        return projectRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project update(Long id, String name, String description) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project with ID " + id + " not found"));

        // Check if new name already exists
        if (!project.getName().equals(name) && projectRepository.existsByName(name)) {
            throw new IllegalArgumentException("Project with name '" + name + "' already exists");
        }

        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    public void deleteById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Project with ID " + id + " not found");
        }
        projectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return projectRepository.existsByName(name);
    }
}
