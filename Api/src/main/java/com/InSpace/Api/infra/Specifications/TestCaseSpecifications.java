package com.InSpace.Api.infra.Specifications;

import com.InSpace.Api.domain.TestCase;
import org.springframework.data.jpa.domain.Specification;

public class TestCaseSpecifications {

    public static Specification<TestCase> hasModuleId(Long moduleId) {
        return SpecificationUtils.joinEqual("module", join -> join.get("moduleId"), moduleId);
    }

    public static Specification<TestCase> hasPriority(Short priority) {
        return SpecificationUtils.equal(root -> root.get("priority"), priority);
    }

    public static Specification<TestCase> nameOrDescriptionContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern));
        };
    }

    public static Specification<TestCase> hasTagName(String tagName) {
        return SpecificationUtils.joinEqual("tags", join -> join.get("name"), tagName);
    }
}
