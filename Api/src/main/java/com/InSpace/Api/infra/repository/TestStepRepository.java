package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestStep;
import com.InSpace.Api.domain.TestScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestStepRepository extends JpaRepository<TestStep, Long>, JpaSpecificationExecutor<TestStep> {

    List<TestStep> findByTestScenarioOrderBySequenceOrder(TestScenario testScenario);

    List<TestStep> findByTestScenario_ScenarioIdOrderBySequenceOrder(Long scenarioId);

    Optional<TestStep> findByTestScenarioAndSequenceOrder(TestScenario testScenario, Integer sequenceOrder);

    boolean existsByTestScenarioAndSequenceOrder(TestScenario testScenario, Integer sequenceOrder);

    @Query("SELECT MAX(t.sequenceOrder) FROM TestStep t WHERE t.testScenario = :scenario")
    Optional<Integer> findMaxSequenceOrderByTestScenario(@Param("scenario") TestScenario testScenario);

    long countByTestScenario(TestScenario testScenario);
}
