package io.github.ronaldobertolucci.mygames.service.mygame;

import io.github.ronaldobertolucci.mygames.infra.exception.ForbiddenException;
import io.github.ronaldobertolucci.mygames.model.company.Company;
import io.github.ronaldobertolucci.mygames.model.company.CompanyRepository;
import io.github.ronaldobertolucci.mygames.model.game.Game;
import io.github.ronaldobertolucci.mygames.model.game.GameRepository;
import io.github.ronaldobertolucci.mygames.model.mygame.*;
import io.github.ronaldobertolucci.mygames.model.platform.Platform;
import io.github.ronaldobertolucci.mygames.model.platform.PlatformRepository;
import io.github.ronaldobertolucci.mygames.model.source.Source;
import io.github.ronaldobertolucci.mygames.model.source.SourceRepository;
import io.github.ronaldobertolucci.mygames.model.user.Role;
import io.github.ronaldobertolucci.mygames.model.user.User;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MyGameServiceTest {

    @MockitoBean
    private Authentication authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private MyGameService myGameService;

    @Test
    @Transactional
    void deveListarSomenteMeuJogoDoUsuarioDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user1 = userRepository.save(getUser(null, "username1", "123456", Role.USER));
        userRepository.save(getUser(null, "username2", "123456", Role.USER));
        myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username1");
        myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username2");

        Page<MyGameDto> myGames = myGameService.findByUser(PageRequest.of(0, 10), "username1");

        assertEquals(1, myGames.getTotalElements());
        assertEquals(user1.getId(), myGames.getContent().getFirst().userId());
    }

    @Test
    @Transactional
    void deveDetalharMeuJogoDoUsuarioDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        userRepository.save(getUser(null, "username", "123456", Role.USER));
        MyGameDto dto1 = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        MyGameDto loaded = myGameService.detail(dto1.id(), "username");

        assertEquals(dto1.id(), loaded.id());
    }

    @Test
    @Transactional
    void deveDetalharMeuJogoDoUsuarioAdmin() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user = userRepository.save(getUser(null, "username", "123456", Role.USER));
        User admin = userRepository.save(getUser(null, "admin", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            MyGameDto loaded = myGameService.detail(dto.id(), "admin");

            assertEquals(dto.id(), loaded.id());
        }
    }

    @Test
    @Transactional
    void deveProibirDetalharMeuJogoParaUsuarioNaoDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user1 = userRepository.save(getUser(null, "username1", "123456", Role.USER));
        User user2 = userRepository.save(getUser(null, "username2", "123456", Role.USER));
        MyGameDto dto1 = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username1");
        MyGameDto dto2 = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username2");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_USER"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(ForbiddenException.class, () -> myGameService.detail(dto1.id(), "username2"));
        }
    }

    @Test
    @Transactional
    void deveSalvarMeuJogoComDadosNecessarios() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        User user = userRepository.save(getUser(null, "username", "123456", Role.USER));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        assertEquals("the witcher", dto.game().title());
        assertEquals("pc", dto.platform().name());
        assertEquals("steam", dto.source().name());
        assertEquals(Status.NOT_PLAYED, dto.status()); // status quando null
    }

    @Test
    @Transactional
    void deveSalvarMeuJogoComDadosNecessariosNoStatusEscolhido() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        User user = userRepository.save(getUser(null, "username", "123456", Role.USER));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), Status.COMPLETED), "username");

        assertEquals("the witcher", dto.game().title());
        assertEquals("pc", dto.platform().name());
        assertEquals("steam", dto.source().name());
        assertEquals(Status.COMPLETED, dto.status()); // status quando null
    }

    @Test
    @Transactional
    void deveAtualizarMeuJogoDoUsuarioDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        userRepository.save(getUser(null, "username", "123456", Role.USER));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        MyGameDto loaded = myGameService.update(new UpdateMyGameDto(dto.id(), game.getId(), platform.getId(), source.getId(), Status.COMPLETED), "username");

        assertEquals(dto.id(), loaded.id());
        assertEquals(Status.COMPLETED, loaded.status());
    }

    @Test
    @Transactional
    void deveAtualizarMeuJogoDoUsuarioAdmin() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user = userRepository.save(getUser(null, "username", "123456", Role.USER));
        User admin = userRepository.save(getUser(null, "admin", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            MyGameDto loaded = myGameService.update(new UpdateMyGameDto(dto.id(), game.getId(), platform.getId(), source.getId(), Status.COMPLETED), "admin");

            assertEquals(dto.id(), loaded.id());
            assertEquals(Status.COMPLETED, loaded.status());
        }
    }

    @Test
    @Transactional
    void deveProibirAtualizarMeuJogoDoUsuarioNaoDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user1 = userRepository.save(getUser(null, "username1", "123456", Role.USER));
        User user2 = userRepository.save(getUser(null, "username2", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username1");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_USER"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(ForbiddenException.class,
                    () -> myGameService.update(new UpdateMyGameDto(dto.id(), game.getId(), platform.getId(),
                            source.getId(), Status.COMPLETED), "username2"));
        }
    }

    @Test
    @Transactional
    void deveAtualizarStatusMeuJogoDoUsuarioDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        userRepository.save(getUser(null, "username", "123456", Role.USER));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        MyGameDto loaded = myGameService.updateStatus(dto.id(), new MyGamesStatusDto(Status.COMPLETED), "username");

        assertEquals(dto.id(), loaded.id());
        assertEquals(Status.COMPLETED, loaded.status());
    }

    @Test
    @Transactional
    void deveAtualizarStatusMeuJogoDoUsuarioAdmin() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user = userRepository.save(getUser(null, "username", "123456", Role.USER));
        User admin = userRepository.save(getUser(null, "admin", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            MyGameDto loaded = myGameService.updateStatus(dto.id(), new MyGamesStatusDto(Status.COMPLETED), "admin");

            assertEquals(dto.id(), loaded.id());
            assertEquals(Status.COMPLETED, loaded.status());
        }
    }

    @Test
    @Transactional
    void deveProibirAtualizarStatusMeuJogoDoUsuarioNaoDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user1 = userRepository.save(getUser(null, "username1", "123456", Role.USER));
        User user2 = userRepository.save(getUser(null, "username2", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username1");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_USER"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(ForbiddenException.class,
                    () -> myGameService.updateStatus(dto.id(), new MyGamesStatusDto(Status.COMPLETED), "username2"));
        }
    }

    @Test
    @Transactional
    void deveDeletarMeuJogoDoUsuarioDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        userRepository.save(getUser(null, "username", "123456", Role.USER));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        assertDoesNotThrow(() -> myGameService.delete(dto.id(), "username"));
    }

    @Test
    @Transactional
    void deveDeletarMeuJogoUsuarioAdmin() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user = userRepository.save(getUser(null, "username", "123456", Role.USER));
        User admin = userRepository.save(getUser(null, "admin", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertDoesNotThrow(() -> myGameService.delete(dto.id(), "admin"));
        }
    }

    @Test
    @Transactional
    void deveProibirDeletarMeuJogoUsuarioNaoDono() {
        Platform platform = platformRepository.save(getPlatform(null, "PC"));
        Source source = sourceRepository.save(getSource(null, "Steam"));
        Company company = companyRepository.save(getCompany(null, "CD Projekt"));
        Game game = gameRepository.save(getGame(null, "The Witcher", company));

        User user = userRepository.save(getUser(null, "username1", "123456", Role.USER));
        User admin = userRepository.save(getUser(null, "username2", "123456", Role.ADMIN));
        MyGameDto dto = myGameService.save(new SaveMyGameDto(game.getId(), platform.getId(), source.getId(), null), "username1");

        try (MockedStatic<SecurityContextHolder> mockSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_USER"));

            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(ForbiddenException.class, () -> myGameService.delete(dto.id(), "username2"));
        }
    }

    private User getUser(Long id, String username, String password, Role role) {
        User user =  new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    private Platform getPlatform(Long id, String name) {
        Platform platform = new Platform();
        platform.setId(id);
        platform.setName(name);
        return platform;
    }

    private Source getSource(Long id, String name) {
        Source source = new Source();
        source.setId(id);
        source.setName(name);
        return source;
    }

    private Company getCompany(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        return company;
    }

    private Game getGame(Long id, String title, Company company) {
        Game game = new Game();
        game.setId(id);
        game.setTitle(title);
        game.setCompany(company);
        return game;
    }
}