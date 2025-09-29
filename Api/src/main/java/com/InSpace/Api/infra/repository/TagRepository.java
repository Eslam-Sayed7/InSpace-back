package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

       Optional<Tag> findByNameIgnoreCase(String name);

       Optional<Tag> findByName(String name);

       List<Tag> findByNameContainingIgnoreCase(String keyword);

       // Count scenarios using a specific tag
       @Query("SELECT COUNT(s) FROM Tag t JOIN t.scenarios s WHERE t.tagId = :tagId")
       long countScenariosUsingTag(@Param("tagId") Long tagId);

       @Query("SELECT COUNT(s) FROM Tag t JOIN t.scenarios s WHERE t.name = :tagName")
       long countScenariosUsingTagName(@Param("tagName") String tagName);

       // Check if tag exists
       boolean existsByName(String name);

       boolean existsByNameIgnoreCase(String name);

       // Get most used tags
       @Query("SELECT t FROM Tag t LEFT JOIN t.scenarios s GROUP BY t.tagId ORDER BY COUNT(s) DESC")
       List<Tag> findMostUsedTags(@Param("limit") int limit);

       // Get unused tags
       @Query("SELECT t FROM Tag t WHERE t.scenarios IS EMPTY")
       List<Tag> findUnusedTags();
}