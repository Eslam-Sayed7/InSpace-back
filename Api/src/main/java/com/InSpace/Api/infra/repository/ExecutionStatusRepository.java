package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExecutionStatusRepository extends JpaRepository<ExecutionStatus, Long>, JpaSpecificationExecutor<ExecutionStatus> {

    Optional<ExecutionStatus> findByName(String name);

    boolean existsByName(String name);
}
