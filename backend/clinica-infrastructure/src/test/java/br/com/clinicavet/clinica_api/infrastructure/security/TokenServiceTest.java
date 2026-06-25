package br.com.clinicavet.clinica_api.infrastructure.security;

import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "chave-secreta-para-teste");
        ReflectionTestUtils.setField(tokenService, "expirationMinutes", 120);
    }

    @Test
    void gerarTokenEGetSubject_ComUsuarioValido_DeveFazerRoundTripDoLogin() {
        Cliente pessoa = new Cliente();
        pessoa.setId(1L);
        pessoa.setNome("Cliente de Teste");
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_CLIENTE, pessoa);
        usuario.setId(1L);

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertEquals("12345678900", tokenService.getSubject(token));
    }

    @Test
    void getSubject_ComTokenInvalido_DeveLancarExcecao() {
        assertThrows(JWTVerificationException.class, () -> tokenService.getSubject("token-invalido"));
    }

    @Test
    void gerarToken_ComUsuarioSemPessoaAssociada_NaoIncluiNomeUsuario() {
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_ADMIN, new Cliente());
        usuario.setId(1L);
        ReflectionTestUtils.setField(usuario, "pessoa", null);

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertEquals("12345678900", tokenService.getSubject(token));
    }

    @Test
    void gerarToken_QuandoAlgoritmoFalhaAoAssinar_LancaRuntimeException() {
        Cliente pessoa = new Cliente();
        pessoa.setId(1L);
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_CLIENTE, pessoa);
        usuario.setId(1L);

        JWTCreationException causaSimulada = new JWTCreationException("Falha simulada ao assinar", new RuntimeException("boom"));

        try (MockedStatic<Algorithm> algorithmMock = Mockito.mockStatic(Algorithm.class)) {
            algorithmMock.when(() -> Algorithm.HMAC256(any(String.class))).thenThrow(causaSimulada);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenService.gerarToken(usuario));

            assertEquals("Erro ao gerar token JWT", exception.getMessage());
            assertSame(causaSimulada, exception.getCause());
        }
    }
}
