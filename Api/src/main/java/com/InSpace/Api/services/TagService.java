package com.InSpace.Api.services;

import com.InSpace.Api.domain.Tag;
import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.infra.repository.TagRepository;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Create a new tag
     */
    public Tag createTag(Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            throw new IllegalArgumentException("Tag with name '" + tag.getName() + "' already exists");
        }
        return tagRepository.save(tag);
    }

    /**
     * Create or get existing tag by name
     */
    public Tag createOrGetTag(String name) {
        Optional<Tag> existingTag = tagRepository.findByName(name);
        if (existingTag.isPresent()) {
            return existingTag.get();
        }
        return tagRepository.save(new Tag(name));
    }

    /**
     * Get tag by ID
     */
    @Transactional(readOnly = true)
    public Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + tagId));
    }

    /**
     * Get tag by name
     */
    @Transactional(readOnly = true)
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with name: " + name));
    }

    /**
     * Get all tags
     */
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    /**
     * Get tags with pagination
     */
    @Transactional(readOnly = true)
    public Page<Tag> getTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    /**
     * Update tag
     */
    public Tag updateTag(Long tagId, Tag tagDetails) {
        Tag existingTag = getTagById(tagId);

        // Check if name is being changed and if new name already exists
        if (!existingTag.getName().equals(tagDetails.getName()) &&
            tagRepository.existsByName(tagDetails.getName())) {
            throw new IllegalArgumentException("Tag with name '" + tagDetails.getName() + "' already exists");
        }

        existingTag.setName(tagDetails.getName());
        return tagRepository.save(existingTag);
    }

    /**
     * Delete tag
     */
    public void deleteTag(Long tagId) {
        Tag tag = getTagById(tagId);
        // Remove tag from all scenarios before deleting
        tag.getScenarios().forEach(scenario -> scenario.getTags().remove(tag));
        tagRepository.delete(tag);
    }

    /**
     * Check if tag exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }

    /**
     * Find tag by name (optional)
     */
    @Transactional(readOnly = true)
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    /**
     * Search tags by name pattern
     */
    @Transactional(readOnly = true)
    public List<Tag> searchTagsByName(String namePattern) {
        return tagRepository.findByNameContainingIgnoreCase(namePattern);
    }

    /**
     * Get tags by scenario
     */
    @Transactional(readOnly = true)
    public Set<Tag> getTagsByScenario(TestScenario scenario) {
        return scenario.getTags();
    }

    /**
     * Get scenarios by tag
     */
    @Transactional(readOnly = true)
    public Set<TestScenario> getScenariosByTag(Tag tag) {
        return tag.getScenarios();
    }

    /**
     * Add tag to scenario
     */
    public void addTagToScenario(Long tagId, TestScenario scenario) {
        Tag tag = getTagById(tagId);
        tag.addScenario(scenario);
        tagRepository.save(tag);
    }

    /**
     * Remove tag from scenario
     */
    public void removeTagFromScenario(Long tagId, TestScenario scenario) {
        Tag tag = getTagById(tagId);
        tag.removeScenario(scenario);
        tagRepository.save(tag);
    }

    /**
     * Create multiple tags from names
     */
    public List<Tag> createTagsFromNames(List<String> tagNames) {
        return tagNames.stream()
                .map(this::createOrGetTag)
                .toList();
    }

    /**
     * Get popular tags (most used)
     */
    @Transactional(readOnly = true)
    public List<Tag> getPopularTags(int limit) {
        return tagRepository.findMostUsedTags(limit);
    }

    /**
     * Get unused tags
     */
    @Transactional(readOnly = true)
    public List<Tag> getUnusedTags() {
        return tagRepository.findUnusedTags();
    }

    /**
     * Clean up unused tags
     */
    public long cleanupUnusedTags() {
        List<Tag> unusedTags = getUnusedTags();
        tagRepository.deleteAll(unusedTags);
        return unusedTags.size();
    }
}
