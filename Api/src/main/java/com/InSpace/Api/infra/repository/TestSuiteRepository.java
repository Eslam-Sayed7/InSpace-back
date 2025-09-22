package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite, Long> {

    Optional<TestSuite> findByNameIgnoreCase(String name);

    List<TestSuite> findByNameContainingIgnoreCase(String keyword);

    List<TestSuite> findByDescriptionContainingIgnoreCase(String keyword);

    boolean existsByName(String name);

    List<TestSuite> findAllByOrderByCreatedAtDesc();
}