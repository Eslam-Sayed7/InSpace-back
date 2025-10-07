package com.InSpace.Api.infra.Specifications;

import com.InSpace.Api.domain.TestScenario;
import org.springframework.data.jpa.domain.Specification;

public class TestScenarioSpecifications {

    public static Specification<TestScenario> hasSuiteId(Long suiteId) {
        return SpecificationUtils.joinEqual("testSuite", join -> join.get("suiteId"), suiteId);
    }

    public static Specification<TestScenario> hasPriority(Short priority) {
        return SpecificationUtils.equal(root -> root.get("priority"), priority);
    }

    public static Specification<TestScenario> nameOrDescriptionContains(String keyword) {
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

    public static Specification<TestScenario> hasTagName(String tagName) {
        return SpecificationUtils.joinEqual("tags", join -> join.get("name"), tagName);
    }
}
