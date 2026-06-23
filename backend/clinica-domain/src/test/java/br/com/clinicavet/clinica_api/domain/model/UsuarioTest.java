package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void criarUsuario_ComLoginNuloOuVazio_DeveLancarExcecao() {
        Cliente pessoa = new Cliente();

        assertThrows(BusinessRuleException.class, () ->
                Usuario.criarUsuario(null, "hash", EnumUsuarioRole.ROLE_CLIENTE, pessoa));

        assertThrows(BusinessRuleException.class, () ->
                Usuario.criarUsuario("  ", "hash", EnumUsuarioRole.ROLE_CLIENTE, pessoa));
    }

    @Test
    void criarUsuario_ComSenhaHashNulaOuVazia_DeveLancarExcecao() {
        Cliente pessoa = new Cliente();

        assertThrows(BusinessRuleException.class, () ->
                Usuario.criarUsuario("12345678900", null, EnumUsuarioRole.ROLE_CLIENTE, pessoa));

        assertThrows(BusinessRuleException.class, () ->
                Usuario.criarUsuario("12345678900", "  ", EnumUsuarioRole.ROLE_CLIENTE, pessoa));
    }

    @Test
    void criarUsuario_ComRoleNula_DeveLancarExcecao() {
        Cliente pessoa = new Cliente();

        assertThrows(BusinessRuleException.class, () ->
                Usuario.criarUsuario("12345678900", "hash", null, pessoa));
    }

    @Test
    void criarUsuario_ComPessoaNula_DeveLancarExcecao() {
        assertThrows(BusinessRuleException.class, () ->
                Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_CLIENTE, null));
    }

    @Test
    void criarUsuario_ComDadosValidos_DeveCriarUsuario() {
        Cliente pessoa = new Cliente();

        Usuario usuario = Usuario.criarUsuario("12345678900", "hash-da-senha", EnumUsuarioRole.ROLE_CLIENTE, pessoa);

        assertEquals("12345678900", usuario.getLogin());
        assertEquals("hash-da-senha", usuario.getSenha());
        assertEquals(EnumUsuarioRole.ROLE_CLIENTE, usuario.getRole());
        assertEquals(pessoa, usuario.getPessoa());
    }

    @Test
    void alterarSenha_ComNovaSenhaNulaOuVazia_DeveLancarExcecao() {
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_CLIENTE, new Cliente());

        assertThrows(BusinessRuleException.class, () -> usuario.alterarSenha(null));
        assertThrows(BusinessRuleException.class, () -> usuario.alterarSenha("  "));
    }

    @Test
    void alterarSenha_ComNovaSenhaValida_DeveAtualizarSenha() {
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash-antigo", EnumUsuarioRole.ROLE_CLIENTE, new Cliente());

        usuario.alterarSenha("hash-novo");

        assertEquals("hash-novo", usuario.getSenha());
    }

    @Test
    void getAuthorities_DeveRetornarAutoridadeComNomeDaRole() {
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash", EnumUsuarioRole.ROLE_ADMIN, new Cliente());

        assertEquals(1, usuario.getAuthorities().size());
        assertTrue(usuario.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void metodosDeUserDetails_DevemRetornarValoresEsperados() {
        Usuario usuario = Usuario.criarUsuario("12345678900", "hash-da-senha", EnumUsuarioRole.ROLE_CLIENTE, new Cliente());

        assertEquals("hash-da-senha", usuario.getPassword());
        assertEquals("12345678900", usuario.getUsername());
        assertTrue(usuario.isAccountNonExpired());
        assertTrue(usuario.isAccountNonLocked());
        assertTrue(usuario.isCredentialsNonExpired());
        assertTrue(usuario.isEnabled());
    }
}
