package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestExecution;
import com.InSpace.Api.domain.TestScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long>, JpaSpecificationExecutor<TestExecution> {

    List<TestExecution> findByRunIdentifierOrderByTestStep_SequenceOrder(UUID runIdentifier);

    List<TestExecution> findByTestScenario_ScenarioIdOrderByStartedAtDesc(Long scenarioId);

    List<TestExecution> findByExecutionStatus_Name(String statusName);

    List<TestExecution> findByTestScenarioAndRunIdentifierOrderByTestStep_SequenceOrder(
        TestScenario testScenario, UUID runIdentifier);

    @Query("SELECT DISTINCT te.runIdentifier FROM TestExecution te WHERE te.testScenario = :scenario ORDER BY MIN(te.startedAt) DESC")
    List<UUID> findDistinctRunIdentifiersByTestScenarioOrderByStartedAtDesc(@Param("scenario") TestScenario testScenario);

    @Query("SELECT te FROM TestExecution te WHERE te.runIdentifier = :runId AND te.executionStatus.name = :statusName")
    List<TestExecution> findByRunIdentifierAndStatusName(@Param("runId") UUID runIdentifier, @Param("statusName") String statusName);

    @Query("SELECT COUNT(te) FROM TestExecution te WHERE te.runIdentifier = :runId AND te.executionStatus.name = 'passed'")
    long countPassedByRunIdentifier(@Param("runId") UUID runIdentifier);

    @Query("SELECT COUNT(te) FROM TestExecution te WHERE te.runIdentifier = :runId AND te.executionStatus.name = 'failed'")
    long countFailedByRunIdentifier(@Param("runId") UUID runIdentifier);

    long countByRunIdentifier(UUID runIdentifier);
}
