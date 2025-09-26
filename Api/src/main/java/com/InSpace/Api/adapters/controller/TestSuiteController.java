package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.TestSuite;
import com.InSpace.Api.services.TestSuiteService;
import com.InSpace.Api.services.dto.ErrorResponse;
import com.InSpace.Api.services.dto.SuccessResponse;
import com.InSpace.Api.services.dto.TestSuite.CreateTestSuiteRequest;
import com.InSpace.Api.services.dto.TestSuite.UpdateTestSuiteRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/testsuites")
@CrossOrigin(origins = "*")
public class TestSuiteController {

    private final TestSuiteService testSuiteService;

    @Autowired
    public TestSuiteController(TestSuiteService testSuiteService) {
        this.testSuiteService = testSuiteService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTestSuite(@RequestBody CreateTestSuiteRequest request) {
        try {
            TestSuite testSuite;
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                testSuite = testSuiteService.createTestSuite(request.getName(), request.getDescription());
            } else {
                testSuite = testSuiteService.createTestSuite(request.getName());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(testSuite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<TestSuite>> getAllTestSuites() {
        List<TestSuite> testSuites = testSuiteService.findAll();
        return ResponseEntity.ok(testSuites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestSuiteById(@PathVariable Long id) {
        Optional<TestSuite> testSuite = testSuiteService.findById(id);
        if (testSuite.isPresent()) {
            return ResponseEntity.ok(testSuite.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Test suite with ID " + id + " not found"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getTestSuiteByName(@RequestParam String name) {
        Optional<TestSuite> testSuite = testSuiteService.findByNameIgnoreCase(name);
        if (testSuite.isPresent()) {
            return ResponseEntity.ok(testSuite.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Test suite with name '" + name + "' not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTestSuite(@PathVariable Long id,
            @RequestBody @Valid UpdateTestSuiteRequest request) {
        try {
            TestSuite updatedTestSuite = testSuiteService.update(id, request.getName(), request.getDescription());
            return ResponseEntity.ok(updatedTestSuite);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestSuite(@PathVariable Long id) {
        try {
            testSuiteService.deleteById(id);
            return ResponseEntity.ok(new SuccessResponse("Test suite deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkTestSuiteExists(@RequestParam String name) {
        boolean exists = testSuiteService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}