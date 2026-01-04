package br.com.bertolucci.mygames.service.platform;

import br.com.bertolucci.mygames.model.platform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

    @Autowired
    private PlatformRepository repository;

    public Page<PlatformDto> findAll(Pageable pageable) {
        Page<Platform> platforms = repository.findAll(pageable);
        return platforms.map(PlatformDto::new);
    }

    public PlatformDto detail(Long id) {
        Platform platform = repository.getReferenceById(id);
        return new PlatformDto(platform);
    }

    @Transactional
    public PlatformDto save(SavePlatformDto dto) {
        Platform platform = new Platform(dto);
        repository.save(platform);
        return new PlatformDto(platform);
    }

    @Transactional
    public PlatformDto update(UpdatePlatformDto dto) {
        Platform platform = repository.getReferenceById(dto.id());
        platform.update(dto);
        return new PlatformDto(platform);
    }

    @Transactional
    public void delete(Long id) {
        Platform platform = repository.getReferenceById(id);
        repository.delete(platform);
    }

}
