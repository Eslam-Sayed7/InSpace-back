package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByNameIgnoreCase(String name);

    List<Project> findByNameContainingIgnoreCase(String keyword);

    List<Project> findByDescriptionContainingIgnoreCase(String keyword);

    boolean existsByName(String name);

    List<Project> findAllByOrderByCreatedAtDesc();
}
