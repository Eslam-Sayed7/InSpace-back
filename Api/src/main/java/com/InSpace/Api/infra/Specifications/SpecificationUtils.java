package com.InSpace.Api.infra.Specifications;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public class SpecificationUtils {

    public static <T, R> Specification<T> equal(Function<Root<T>, Expression<R>> fieldAccessor, R value) {
        return (root, query, cb) -> value == null
                ? cb.conjunction()
                : cb.equal(fieldAccessor.apply(root), value);
    }

    public static <T> Specification<T> likeIgnoreCase(Function<Root<T>, Expression<String>> fieldAccessor,
            String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(fieldAccessor.apply(root)), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static <T, J, R> Specification<T> joinEqual(
            String relation,
            Function<Join<T, J>, Expression<R>> fieldAccessor,
            R value) {
        return (root, query, cb) -> {
            if (value == null)
                return cb.conjunction();
            Join<T, J> join = root.join(relation, JoinType.INNER);
            return cb.equal(fieldAccessor.apply(join), value);
        };
    }
}
