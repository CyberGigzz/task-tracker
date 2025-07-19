package com.example.tasktracker.repository;

import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.TaskPriority;
import com.example.tasktracker.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> build(Long projectId, TaskStatus status, TaskPriority priority) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("project").get("id"), projectId));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}