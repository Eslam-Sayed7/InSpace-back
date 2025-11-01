package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestCase;
import com.InSpace.Api.domain.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestStepRepository extends JpaRepository<TestStep, Long>, JpaSpecificationExecutor<TestStep> {

    List<TestStep> findByTestCaseOrderBySequenceOrder(TestCase testCase);

    List<TestStep> findByTestCase_TestcaseIdOrderBySequenceOrder(Long testcaseId);

    Optional<TestStep> findByTestCaseAndSequenceOrder(TestCase testCase, Integer sequenceOrder);

    boolean existsByTestCaseAndSequenceOrder(TestCase testCase, Integer sequenceOrder);

    @Query("SELECT MAX(t.sequenceOrder) FROM TestStep t WHERE t.testCase = :testCase")
    Optional<Integer> findMaxSequenceOrderByTestCase(@Param("testCase") TestCase testCase);

    long countByTestCase(TestCase testCase);
}
