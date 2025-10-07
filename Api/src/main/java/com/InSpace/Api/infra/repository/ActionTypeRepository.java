package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionType, Long>, JpaSpecificationExecutor<ActionType> {

    Optional<ActionType> findByName(String name);

    boolean existsByName(String name);
}
