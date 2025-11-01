package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.TestCase;
import com.InSpace.Api.domain.Module;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface TestCaseRepository
        extends JpaRepository<TestCase, Long>, JpaSpecificationExecutor<TestCase> {

    boolean existsByNameAndModule(String name, Module module);

    Page<TestCase> findAll(Specification<TestCase> spec, Pageable pageable);

    @Query("SELECT DISTINCT t.priority FROM TestCase t ORDER BY t.priority ASC")
    List<Short> findDistinctPriorities();


}
