package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.BatchVerification;
import com.InSpace.Api.domain.TestAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchVerificationRepository extends JpaRepository<BatchVerification, Long>, JpaSpecificationExecutor<BatchVerification> {

    List<BatchVerification> findByTestAction(TestAction testAction);

    List<BatchVerification> findByTestAction_ActionId(Long actionId);

    List<BatchVerification> findByVerificationStatus(String verificationStatus);

    List<BatchVerification> findByCriteriaType(String criteriaType);

    @Query("SELECT bv FROM BatchVerification bv WHERE bv.testAction.testStep.testScenario.scenarioId = :scenarioId ORDER BY bv.testAction.testStep.sequenceOrder, bv.testAction.executionOrder")
    List<BatchVerification> findByScenarioIdOrderByStepAndAction(@Param("scenarioId") Long scenarioId);

    @Query("SELECT bv FROM BatchVerification bv WHERE bv.verificationStatus = 'pending' ORDER BY bv.createdAt")
    List<BatchVerification> findPendingVerificationsOrderByCreatedAt();

    @Query("SELECT COUNT(bv) FROM BatchVerification bv WHERE bv.testAction = :testAction AND bv.verificationStatus = :status")
    long countByTestActionAndStatus(@Param("testAction") TestAction testAction, @Param("status") String status);

    @Query("SELECT bv FROM BatchVerification bv WHERE bv.testAction.testStep.stepId = :stepId ORDER BY bv.testAction.executionOrder")
    List<BatchVerification> findByStepIdOrderByActionExecution(@Param("stepId") Long stepId);
}
