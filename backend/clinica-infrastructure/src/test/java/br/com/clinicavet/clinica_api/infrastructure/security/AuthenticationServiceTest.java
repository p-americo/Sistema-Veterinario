package br.com.clinicavet.clinica_api.infrastructure.security;

import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(usuarioRepository);
    }

    @Test
    void loadUserByUsername_Sucesso() {
        Cliente pessoa = new Cliente();
        pessoa.setId(1L);
        pessoa.setNome("Cliente de Teste");
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_CLIENTE, pessoa);
        when(usuarioRepository.findByLogin("12345678900")).thenReturn(Optional.of(usuario));

        UserDetails resultado = authenticationService.loadUserByUsername("12345678900");

        assertNotNull(resultado);
        assertEquals("12345678900", resultado.getUsername());
    }

    @Test
    void loadUserByUsername_ExceptionUsuarioNaoEncontrado() {
        when(usuarioRepository.findByLogin("00000000000")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.loadUserByUsername("00000000000");
        });

        assertEquals("Usuário não encontrado: 00000000000", exception.getMessage());
    }
}
