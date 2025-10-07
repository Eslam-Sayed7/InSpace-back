package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.services.TestScenarioService;
import com.InSpace.Api.services.dto.ErrorResponse;
import com.InSpace.Api.services.dto.TestScenario.Requests.CreateTestScenarioRequest;
import com.InSpace.Api.services.dto.TestScenario.Response.TestScenarioDTO;
import com.InSpace.Api.services.dto.mapperes.TestScenarioMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/testscenarios")
@CrossOrigin(origins = "*")
public class TestScenarioController {

    private final TestScenarioService testScenarioService;
    private final TestScenarioMapper testScenarioMapper = new TestScenarioMapper();

    @Autowired
    public TestScenarioController(TestScenarioService testScenarioService) {
        this.testScenarioService = testScenarioService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTestScenario(
            @RequestBody @Valid CreateTestScenarioRequest request) {
        try {
            TestScenario scenario = testScenarioService.createTestScenarioWithTags(
                    request.getName(),
                    request.getDescription(),
                    request.getPriority(),
                    request.getSuiteId(),
                    request.getTagNames());
            return ResponseEntity.status(HttpStatus.CREATED).body(testScenarioMapper.toDto(scenario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestScenarioById(@PathVariable Long id) {
        Optional<TestScenario> scenario = testScenarioService.findById(id);
        return scenario.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Test scenario with ID " + id + " not found")));
    }

    @GetMapping("/suite/{suiteId}")
    public ResponseEntity<?> getBySuite(
            @PathVariable Long suiteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                testScenarioService.findBySuite(suiteId, page, size, sortBy, sortDir));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<?> getByPriority(
            @PathVariable Short priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(
                testScenarioService.findByPriority(priority, page, size, sortBy, sortDir));
    }

    @GetMapping("/suite/{suiteId}/search")
    public ResponseEntity<?> searchInSuite(
            @PathVariable Long suiteId,
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String sortBy,
            @RequestParam(defaultValue = "") String sortDir) {

        var scenariosPage = testScenarioService.searchScenariosInSuite(suiteId, searchTerm, page, size, sortBy,
                sortDir);
        List<TestScenarioDTO> result = new ArrayList<>();
        for (TestScenario scenario : scenariosPage.getContent()) {
            TestScenarioDTO dto = testScenarioMapper.toDto(scenario);
            result.add(dto);
        }
        return ResponseEntity.ok(result);
    }
}
