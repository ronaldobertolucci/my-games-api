package io.github.ronaldobertolucci.mygames.service.platform;

import io.github.ronaldobertolucci.mygames.model.platform.*;
import io.github.ronaldobertolucci.mygames.model.platform.*;
import io.github.ronaldobertolucci.mygames.model.store.Store;
import io.github.ronaldobertolucci.mygames.model.store.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private StoreRepository storeRepository;

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
        Store store = storeRepository.getReferenceById(dto.storeId());

        Platform platform = new Platform(dto.name(), store);
        platformRepository.save(platform);
        return new PlatformDto(platform);
    }

    @Transactional
    public PlatformDto update(UpdatePlatformDto dto) {
        Store store = storeRepository.getReferenceById(dto.storeId());

        Platform platform = platformRepository.getReferenceById(dto.id());
        platform.update(dto.name(), store);
        return new PlatformDto(platform);
    }

    @Transactional
    public void delete(Long id) {
        Platform platform = platformRepository.getReferenceById(id);
        platformRepository.delete(platform);
    }

}
