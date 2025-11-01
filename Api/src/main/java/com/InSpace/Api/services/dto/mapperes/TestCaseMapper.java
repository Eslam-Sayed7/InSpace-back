package com.InSpace.Api.services.dto.mapperes;

import com.InSpace.Api.domain.TestCase;
import com.InSpace.Api.services.dto.Testcase.Response.TestCaseDTO;

public class TestCaseMapper {

    public static TestCaseDTO toDto(TestCase entity) {
        if (entity == null) {
            return null;
        }

        TestCaseDTO dto = new TestCaseDTO();
        dto.setTestcaseId(entity.getTestcaseId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}
