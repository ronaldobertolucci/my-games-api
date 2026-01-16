package io.github.ronaldobertolucci.mygames.service.source;

import io.github.ronaldobertolucci.mygames.model.source.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SourceService {

    @Autowired
    private SourceRepository repository;

    public Page<SourceDto> findByNameContaining(String name, Pageable pageable) {
        Page<Source> sources = repository.findSourceByNameContaining(name, pageable);
        return sources.map(SourceDto::new);
    }

    public List<SourceDto> findByNameContaining(String name) {
        List<Source> sources = repository.findSourceByNameContaining(name);
        return sources.stream().map(SourceDto::new).toList();
    }

    public Page<SourceDto> findAll(Pageable pageable) {
        Page<Source> sources = repository.findAll(pageable);
        return sources.map(SourceDto::new);
    }

    public List<SourceDto> findAll() {
        List<Source> sources = repository.findAll();
        return sources.stream().map(SourceDto::new).toList();
    }

    public SourceDto detail(Long id) {
        Source source = repository.getReferenceById(id);
        return new SourceDto(source);
    }

    @Transactional
    public SourceDto save(SaveSourceDto dto) {
        Source source = new Source(dto);
        repository.save(source);
        return new SourceDto(source);
    }

    @Transactional
    public SourceDto update(UpdateSourceDto dto) {
        Source source = repository.getReferenceById(dto.id());
        source.update(dto);
        return new SourceDto(source);
    }

    @Transactional
    public void delete(Long id) {
        Source source = repository.getReferenceById(id);
        repository.delete(source);
    }

}
