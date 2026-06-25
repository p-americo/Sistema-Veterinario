package br.com.clinicavet.clinica_api.infrastructure.security;

import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FilterChain filterChain;

    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        securityFilter = new SecurityFilter(tokenService, usuarioRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_SemHeaderAuthorization_NaoAutentica() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService, never()).getSubject(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ComTokenValido_Autentica() throws Exception {
        Cliente pessoa = new Cliente();
        pessoa.setId(1L);
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_CLIENTE, pessoa);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.getSubject("token-valido")).thenReturn("12345678900");
        when(usuarioRepository.findByLogin("12345678900")).thenReturn(Optional.of(usuario));

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ComTokenInvalido_NaoAutenticaEContinuaFiltro() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.getSubject("token-invalido")).thenThrow(new RuntimeException("Token JWT inválido"));

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ComUsuarioNaoEncontrado_NaoAutenticaEContinuaFiltro() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.getSubject("token-valido")).thenReturn("inexistente");
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
