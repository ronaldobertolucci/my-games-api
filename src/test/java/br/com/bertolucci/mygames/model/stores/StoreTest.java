package br.com.bertolucci.mygames.model.stores;

import br.com.bertolucci.mygames.model.store.Store;
import br.com.bertolucci.mygames.model.store.StoreRepository;
import br.com.bertolucci.mygames.model.store.UpdateStoreDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class StoreTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private StoreRepository repository;

    @Test
    void deveSalvarCompanhiaComNome() {
        // given
        var store = new Store();
        store.setName("Store name ");
        em.persist(store);

        //then
        Store loadedStore = repository.getReferenceById(store.getId());
        assertEquals("store name", loadedStore.getName());
    }

    @Test
    void deveAtualizarCompanhiaComNome() {
        // given
        var store = new Store();
        store.setName("Store name ");
        em.persist(store);

        Store loadedStore = repository.getReferenceById(store.getId());

        //then
        loadedStore.update(new UpdateStoreDto(loadedStore.getId(), "New name"));
        assertEquals("new name", loadedStore.getName());
    }
}
