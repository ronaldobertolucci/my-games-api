package br.com.bertolucci.mygames.model.platform;

import br.com.bertolucci.mygames.model.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
}
