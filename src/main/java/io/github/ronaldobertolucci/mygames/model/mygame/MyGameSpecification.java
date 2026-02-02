package io.github.ronaldobertolucci.mygames.model.mygame;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MyGameSpecification {
    
    public static Specification<MyGame> byFilter(MyGameFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Username é obrigatório
            predicates.add(cb.equal(root.get("user").get("username"), filter.getUsername()));
            
            // Filtros opcionais
            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("game").get("title")), 
                    "%" + filter.getTitle().toLowerCase() + "%"
                ));
            }
            
            if (filter.getSourceId() != null) {
                predicates.add(cb.equal(root.get("source").get("id"), filter.getSourceId()));
            }
            
            if (filter.getPlatformId() != null) {
                predicates.add(cb.equal(root.get("platform").get("id"), filter.getPlatformId()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}