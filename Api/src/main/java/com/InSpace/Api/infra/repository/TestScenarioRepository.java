package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.TestSuite;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TestScenarioRepository
        extends JpaRepository<TestScenario, Long>, JpaSpecificationExecutor<TestScenario> {

    boolean existsByNameAndTestSuite(String name, TestSuite testSuite);

    Page<TestScenario> findDistinctByTags_NameIn(Set<String> tagNames, Pageable pageable);
}