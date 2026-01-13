package io.github.ronaldobertolucci.mygames.service.source;

import io.github.ronaldobertolucci.mygames.model.source.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SourceService {

    @Autowired
    private SourceRepository repository;

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
