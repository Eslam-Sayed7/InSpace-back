package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestScenarioRepository extends JpaRepository<TestScenario, Long> {

       List<TestScenario> findByTestSuite(TestSuite testSuite);

       List<TestScenario> findByTestSuiteSuiteId(Long suiteId);

       Optional<TestScenario> findByName(String name);

       List<TestScenario> findByNameContainingIgnoreCase(String keyword);

       // Find scenarios with specific tags
       @Query("SELECT DISTINCT ts FROM TestScenario ts JOIN ts.tags t WHERE t.name IN :tagNames")
       List<TestScenario> findByTagNames(@Param("tagNames") List<String> tagNames);

       @Query("SELECT DISTINCT ts FROM TestScenario ts JOIN ts.tags t WHERE t.tagId IN :tagIds")
       List<TestScenario> findByTagIds(@Param("tagIds") List<Long> tagIds);

       // Search scenarios by keyword in name or description
       @Query("SELECT ts FROM TestScenario ts WHERE " +
                     "LOWER(ts.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(ts.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       List<TestScenario> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm);

       // Find scenarios by suite with search
       @Query("SELECT ts FROM TestScenario ts WHERE ts.testSuite.suiteId = :suiteId AND " +
                     "(LOWER(ts.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(ts.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
       List<TestScenario> findBySuiteIdAndSearch(@Param("suiteId") Long suiteId,
                     @Param("searchTerm") String searchTerm);

       // Ordering methods
       List<TestScenario> findByTestSuiteSuiteIdOrderByPriorityAscNameAsc(Long suiteId);

       List<TestScenario> findByPriority(Short priority);

       // Count methods
       long countByTestSuite(TestSuite testSuite);

       long countByTestSuiteSuiteId(Long suiteId);

       boolean existsByNameAndTestSuiteSuiteId(String name, Long suiteId);

       boolean existsByNameAndTestSuite(String name, TestSuite testSuite);

       // Delete operations
       void deleteByTestSuite(TestSuite testSuite);

       void deleteByTestSuiteSuiteId(Long suiteId);
}