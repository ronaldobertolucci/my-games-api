package io.github.ronaldobertolucci.mygames.model.source;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    List<Source> findSourceByNameContaining(String name);
    Page<Source> findSourceByNameContaining(String name, Pageable pageable);
}
