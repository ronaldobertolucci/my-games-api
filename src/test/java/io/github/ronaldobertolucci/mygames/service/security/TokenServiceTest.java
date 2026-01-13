package io.github.ronaldobertolucci.mygames.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github.ronaldobertolucci.mygames.model.user.Role;
import io.github.ronaldobertolucci.mygames.model.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private static final String TEST_PASSWORD = "test-secret-password";
    private UserDto testUser;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        // Injeta o valor do password usando ReflectionTestUtils
        ReflectionTestUtils.setField(tokenService, "password", TEST_PASSWORD);

        testUser = new UserDto(1L, "testuser", Role.USER, true);;
    }

    @Test
    void deveGerarTokenJWTValidoComTodasAsClaimsCorretas() {
        String token = tokenService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        var algorithm = Algorithm.HMAC256(TEST_PASSWORD);
        var decodedJWT = JWT.require(algorithm)
                .withIssuer("My Games API")
                .build()
                .verify(token);

        assertEquals("testuser", decodedJWT.getSubject());
        assertEquals(1L, decodedJWT.getClaim("id").asLong());
        assertEquals("My Games API", decodedJWT.getIssuer());
        assertNotNull(decodedJWT.getExpiresAt());
    }

    @Test
    void deveGerarTokensDiferentesParaOMesmoUsuarioEmMomentosDiferentes() throws InterruptedException {
        String token1 = tokenService.generateToken(testUser);
        Thread.sleep(1000); // Aguarda 1 segundo para garantir timestamp diferente
        String token2 = tokenService.generateToken(testUser);

        assertNotEquals(token1, token2);
    }

    @Test
    void deveExtrairOSubjectCorretamenteDeUmTokenValido() {
        String token = tokenService.generateToken(testUser);

        String subject = tokenService.getSubject(token);

        assertEquals("testuser", subject);
    }

    @Test
    void deveLancarExcecaoAoTentarVerificarTokenInvalido() {
        String invalidToken = "invalid.token.here";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(invalidToken);
        });

        assertEquals("Token JWT inválido ou expirado!", exception.getMessage());
        assertInstanceOf(JWTVerificationException.class, exception.getCause());
    }

    @Test
    void deveLancarExcecaoAoTentarVerificarTokenComAssinaturaIncorreta() {
        var wrongAlgorithm = Algorithm.HMAC256("wrong-password");
        String tokenWithWrongSignature = JWT.create()
                .withIssuer("My Games API")
                .withSubject("testuser")
                .withClaim("id", 1L)
                .sign(wrongAlgorithm);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(tokenWithWrongSignature);
        });

        assertEquals("Token JWT inválido ou expirado!", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoTentarVerificarTokenComIssuerIncorreto() {
        var algorithm = Algorithm.HMAC256(TEST_PASSWORD);
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("Wrong Issuer")
                .withSubject("testuser")
                .withClaim("id", 1L)
                .sign(algorithm);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(tokenWithWrongIssuer);
        });

        assertEquals("Token JWT inválido ou expirado!", exception.getMessage());
    }

    @Test
    void deveGerarTokenQueExpiraEmAproximadamenteDuasHoras() {
        String token = tokenService.generateToken(testUser);

        var algorithm = Algorithm.HMAC256(TEST_PASSWORD);
        var decodedJWT = JWT.require(algorithm)
                .withIssuer("My Games API")
                .build()
                .verify(token);

        Instant expiresAt = decodedJWT.getExpiresAt().toInstant();
        Instant now = Instant.now();

        // Verifica se expira entre 1h59min e 2h01min (margem de tolerância)
        long minutesUntilExpiration = ChronoUnit.MINUTES.between(now, expiresAt);
        assertTrue(minutesUntilExpiration >= 119 && minutesUntilExpiration <= 121,
                "Token deve expirar em aproximadamente 2 horas, mas expira em " + minutesUntilExpiration + " minutos");
    }

    @Test
    void deveGerarTokenComIdDoUsuarioCorreto() {
        UserDto userWithDifferentId = new UserDto(999L, "anotheruser", Role.USER, true);

        String token = tokenService.generateToken(userWithDifferentId);

        var algorithm = Algorithm.HMAC256(TEST_PASSWORD);
        var decodedJWT = JWT.require(algorithm)
                .withIssuer("My Games API")
                .build()
                .verify(token);

        assertEquals(999L, decodedJWT.getClaim("id").asLong());
        assertEquals("anotheruser", decodedJWT.getSubject());
    }

    @Test
    void deveProcessarCorretamenteUsuarioComUsernameVazio() {
        UserDto userWithEmptyUsername = new UserDto(1L, "", Role.USER, true);

        String token = tokenService.generateToken(userWithEmptyUsername);
        String subject = tokenService.getSubject(token);

        assertEquals("", subject);
    }

    @Test
    void deveLancarExcecaoAoTentarVerificarTokenNull() {
        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(null);
        });
    }

    @Test
    void deveLancarExcecaoAoTentarVerificarTokenVazio() {
        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject("");
        });
    }
}