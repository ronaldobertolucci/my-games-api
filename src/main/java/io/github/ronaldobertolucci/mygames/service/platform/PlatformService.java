package io.github.ronaldobertolucci.mygames.service.platform;

import io.github.ronaldobertolucci.mygames.model.platform.*;
import io.github.ronaldobertolucci.mygames.model.platform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

    @Autowired
    private PlatformRepository platformRepository;

    public Page<PlatformDto> findAll(Pageable pageable) {
        Page<Platform> platforms = platformRepository.findAll(pageable);
        return platforms.map(PlatformDto::new);
    }

    public PlatformDto detail(Long id) {
        Platform platform = platformRepository.getReferenceById(id);
        return new PlatformDto(platform);
    }

    @Transactional
    public PlatformDto save(SavePlatformDto dto) {
        Platform platform = new Platform(dto);
        platformRepository.save(platform);
        return new PlatformDto(platform);
    }

    @Transactional
    public PlatformDto update(UpdatePlatformDto dto) {
        Platform platform = platformRepository.getReferenceById(dto.id());
        platform.update(dto);
        return new PlatformDto(platform);
    }

    @Transactional
    public void delete(Long id) {
        Platform platform = platformRepository.getReferenceById(id);
        platformRepository.delete(platform);
    }

}
