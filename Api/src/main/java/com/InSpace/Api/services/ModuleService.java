package com.InSpace.Api.services;

import com.InSpace.Api.domain.Module;
import com.InSpace.Api.infra.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Module save(Module module) {
        return moduleRepository.save(module);
    }

    public Module createModule(String name) {
        if (moduleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Module with name '" + name + "' already exists");
        }
        Module module = new Module(name);
        return moduleRepository.save(module);
    }

    public Module createModule(String name, String description) {
        if (moduleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Module with name '" + name + "' already exists");
        }
        Module module = new Module(name, description);
        return moduleRepository.save(module);
    }

    public Module createModule(String name, String description, String prerequisite) {
        if (moduleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Module with name '" + name + "' already exists");
        }
        Module module = new Module(name, description);
        return moduleRepository.save(module);
    }

    @Transactional(readOnly = true)
    public Optional<Module> findById(Long id) {
        return moduleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Module> findByNameIgnoreCase(String name) {
        return moduleRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Module> findAll() {
        return moduleRepository.findAll();
    }

    public Module update(Long id, String name, String description) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module with ID " + id + " not found"));

        // Check if new name already exists
        if (!module.getName().equals(name) && moduleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Module with name '" + name + "' already exists");
        }

        module.setName(name);
        module.setDescription(description);
        return moduleRepository.save(module);
    }

    public void deleteById(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Module with ID " + id + " not found");
        }
        moduleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return moduleRepository.existsByName(name);
    }
}
