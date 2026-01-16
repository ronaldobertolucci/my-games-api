package io.github.ronaldobertolucci.mygames.model.platform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    List<Platform> findPlatformsByNameContaining(String name);
    Page<Platform> findPlatformsByNameContaining(String name, Pageable pageable);
}
