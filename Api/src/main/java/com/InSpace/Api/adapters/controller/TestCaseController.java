package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.TestCase;
import com.InSpace.Api.services.TestCaseService;
import com.InSpace.Api.services.dto.ErrorResponse;
import com.InSpace.Api.services.dto.Testcase.Requests.CreateTestcaseRequest;
import com.InSpace.Api.services.dto.Testcase.Response.TestCaseDTO;
import com.InSpace.Api.services.dto.mapperes.TestCaseMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/testcases")
@CrossOrigin(origins = "*")
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final TestCaseMapper testCaseMapper = new TestCaseMapper();

    @Autowired
    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTestcase(
            @RequestBody @Valid CreateTestcaseRequest request) {
        try {
            TestCase testcase = testCaseService.createTestcase(
                    request.getName(),
                    request.getDescription(),
                    request.getPriority(),
                    request.getModuleId());
            return ResponseEntity.status(HttpStatus.CREATED).body(testCaseMapper.toDto(testcase));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestcaseById(@PathVariable Long id) {
        Optional<TestCase> testcase = testCaseService.findById(id);
        return testcase.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Testcase with ID " + id + " not found")));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<?> getByModule(
            @PathVariable Long moduleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                testCaseService.findByModule(moduleId, page, size, sortBy, sortDir));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<?> getByPriority(
            @PathVariable Short priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                testCaseService.findByPriority(priority, page, size, sortBy, sortDir));
    }

    @GetMapping("/module/{moduleId}/search")
    public ResponseEntity<?> searchInModule(
            @PathVariable Long moduleId,
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String sortBy,
            @RequestParam(defaultValue = "") String sortDir) {

        var pageResult = testCaseService.searchTestcasesInModule(moduleId, searchTerm, page, size, sortBy,
                sortDir);
        List<TestCaseDTO> result = new ArrayList<>();
        for (TestCase testcase : pageResult.getContent()) {
            TestCaseDTO dto = testCaseMapper.toDto(testcase);
            result.add(dto);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<Short>> getAvailablePriorities() {
        List<Short> priorities = testCaseService.findAvailablePriorities();
        return ResponseEntity.ok(priorities);
    }
}
