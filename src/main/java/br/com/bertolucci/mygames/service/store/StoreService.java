package br.com.bertolucci.mygames.service.store;

import br.com.bertolucci.mygames.model.store.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;

    public Page<StoreDto> findAll(Pageable pageable) {
        Page<Store> stores = repository.findAll(pageable);
        return stores.map(StoreDto::new);
    }

    public StoreDto detail(Long id) {
        Store store = repository.getReferenceById(id);
        return new StoreDto(store);
    }

    @Transactional
    public StoreDto save(SaveStoreDto dto) {
        Store store = new Store(dto);
        repository.save(store);
        return new StoreDto(store);
    }

    @Transactional
    public StoreDto update(UpdateStoreDto dto) {
        Store store = repository.getReferenceById(dto.id());
        store.update(dto);
        return new StoreDto(store);
    }

    @Transactional
    public void delete(Long id) {
        Store store = repository.getReferenceById(id);
        repository.delete(store);
    }

}
