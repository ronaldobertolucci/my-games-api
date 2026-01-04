package br.com.bertolucci.mygames.model.platform;

import br.com.bertolucci.mygames.model.store.Store;
import br.com.bertolucci.mygames.model.store.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PlatformTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void deveSalvarPlataformaComDadosCorretos() {
        // given
        var store = new Store();
        store.setName("Store name");
        em.persist(store);

        var platform = new Platform();
        platform.setName("Platform name ");
        platform.setStore(store);
        em.persist(platform);

        //then
        Platform loadedPlatform = platformRepository.getReferenceById(platform.getId());
        assertEquals("platform name", loadedPlatform.getName());
        assertEquals(store.getId(), loadedPlatform.getStore().getId());
    }

    @Test
    void deveSalvarQuandoMesmoNomeMasLojaDiferente() {
        var store1 = new Store();
        store1.setName("Store name");
        em.persist(store1);

        var platform1 = new Platform();
        platform1.setName("Platform name ");
        platform1.setStore(store1);
        em.persist(platform1);

        var store2 = new Store();
        store2.setName("Store name 2");
        em.persist(store2);

        var platform2 = new Platform();
        platform2.setName("Platform name ");
        platform2.setStore(store2);
        em.persist(platform2);
    }

    @Test
    void deveSalvarQuandoMesmaLojaMasNomeDiferente() {
        var store1 = new Store();
        store1.setName("Store name");
        em.persist(store1);

        var platform1 = new Platform();
        platform1.setName("Platform name ");
        platform1.setStore(store1);
        em.persist(platform1);

        var platform2 = new Platform();
        platform2.setName("Platform name 2");
        platform2.setStore(store1);
        em.persist(platform2);
    }

    @Test
    void deveFalharQuandoMesmaLojaMesmoNome() {
        var store1 = new Store();
        store1.setName("Store name");
        em.persist(store1);

        var platform1 = new Platform();
        platform1.setName("Platform name");
        platform1.setStore(store1);
        em.persist(platform1);

        var platform2 = new Platform();
        platform2.setName("Platform name");
        platform2.setStore(store1);
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> em.persist(platform2));
    }

    @Test
    void deveSalvarCompanhiaComNome() {
        // given
        var store = new Store();
        store.setName("Store name");
        em.persist(store);

        var platform = new Platform();
        platform.setName("Platform name ");
        platform.setStore(store);
        em.persist(platform);

        //then
        Platform loadedPlatform = platformRepository.getReferenceById(platform.getId());
        assertEquals("platform name", loadedPlatform.getName());
        assertEquals(store.getId(), loadedPlatform.getStore().getId());
    }

    @Test
    void deveAtualizarCompanhiaComNome() {
        // given
        var store1 = new Store();
        store1.setName("Store name");
        em.persist(store1);

        var store2 = new Store();
        store2.setName("Store name 2");
        em.persist(store2);

        var platform = new Platform();
        platform.setName("Platform name ");
        platform.setStore(store1);
        em.persist(platform);

        Platform loadedPlatform = platformRepository.getReferenceById(platform.getId());
        Store loadedStore = storeRepository.getReferenceById(store2.getId());

        //then
        loadedPlatform.update("New name ", loadedStore);
        assertEquals("new name", loadedPlatform.getName());
        assertEquals(store2.getId(), loadedPlatform.getStore().getId());
    }
}
