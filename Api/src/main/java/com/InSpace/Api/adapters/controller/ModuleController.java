package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.Module;
import com.InSpace.Api.services.ModuleService;
import com.InSpace.Api.services.dto.ErrorResponse;
import com.InSpace.Api.services.dto.SuccessResponse;
import com.InSpace.Api.services.dto.Module.CreateModuleRequest;
import com.InSpace.Api.services.dto.Module.UpdateModuleRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleController {

    private final ModuleService moduleService;

    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createModule(@RequestBody CreateModuleRequest request) {
        try {
            Module module = moduleService.createModule(request.getName(), request.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(module);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Module>> getAllModules() {
        List<Module> modules = moduleService.findAll();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getModuleById(@PathVariable Long id) {
        Optional<Module> module = moduleService.findById(id);
        if (module.isPresent()) {
            return ResponseEntity.ok(module.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Module with ID " + id + " not found"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getModuleByName(@RequestParam String name) {
        Optional<Module> module = moduleService.findByNameIgnoreCase(name);
        if (module.isPresent()) {
            return ResponseEntity.ok(module.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Module with name '" + name + "' not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(@PathVariable Long id,
            @RequestBody @Valid UpdateModuleRequest request) {
        try {
            Module updatedModule = moduleService.update(id, request.getName(), request.getDescription());
            return ResponseEntity.ok(updatedModule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@PathVariable Long id) {
        try {
            moduleService.deleteById(id);
            return ResponseEntity.ok(new SuccessResponse("Module deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkModuleExists(@RequestParam String name) {
        boolean exists = moduleService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}
