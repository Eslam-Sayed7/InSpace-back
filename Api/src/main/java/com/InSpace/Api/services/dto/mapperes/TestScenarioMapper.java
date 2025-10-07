package com.InSpace.Api.services.dto.mapperes;

import com.InSpace.Api.domain.TestScenario;
import com.InSpace.Api.domain.Tag;
import com.InSpace.Api.services.dto.TestScenario.Response.TestScenarioDTO;
import java.util.stream.Collectors;

public class TestScenarioMapper {

    public static TestScenarioDTO toDto(TestScenario entity) {
        if (entity == null) {
            return null;
        }

        TestScenarioDTO dto = new TestScenarioDTO();
        dto.setScenarioId(entity.getScenarioId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Convert tags -> names
        if (entity.getTags() != null) {
            dto.setTags(
                    entity.getTags().stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet()));
        }

        return dto;
    }
}
