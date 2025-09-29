package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestAction;
import com.InSpace.Api.domain.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestActionRepository extends JpaRepository<TestAction, Long>, JpaSpecificationExecutor<TestAction> {

    List<TestAction> findByTestStepOrderByExecutionOrder(TestStep testStep);

    List<TestAction> findByTestStep_StepIdOrderByExecutionOrder(Long stepId);

    List<TestAction> findByActionType(String actionType);

    List<TestAction> findByVerifyImmediately(Boolean verifyImmediately);

    @Query("SELECT ta FROM TestAction ta WHERE ta.testStep.testScenario.scenarioId = :scenarioId ORDER BY ta.testStep.sequenceOrder, ta.executionOrder")
    List<TestAction> findByScenarioIdOrderByStepAndExecution(@Param("scenarioId") Long scenarioId);

    @Query("SELECT ta FROM TestAction ta WHERE ta.testStep = :testStep AND ta.verifyImmediately = true ORDER BY ta.executionOrder")
    List<TestAction> findImmediateVerificationsByTestStep(@Param("testStep") TestStep testStep);

    long countByTestStep(TestStep testStep);
}
