package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    Optional<Module> findByNameIgnoreCase(String name);

    List<Module> findByNameContainingIgnoreCase(String keyword);

    List<Module> findByDescriptionContainingIgnoreCase(String keyword);

    boolean existsByName(String name);

    List<Module> findAllByOrderByCreatedAtDesc();
}
