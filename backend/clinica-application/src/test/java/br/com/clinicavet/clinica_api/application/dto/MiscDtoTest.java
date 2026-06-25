package br.com.clinicavet.clinica_api.application.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MiscDtoTest {

    @Test
    void loginDTO_GettersESetters() {
        LoginDTO dto = new LoginDTO();
        dto.setLogin("12345678901");
        dto.setSenha("Senha@123");

        assertEquals("12345678901", dto.getLogin());
        assertEquals("Senha@123", dto.getSenha());
    }

    @Test
    void senhaUpdateDTO_GettersESetters() {
        SenhaUpdateDTO dto = new SenhaUpdateDTO();
        dto.setSenhaAtual("antiga");
        dto.setNovaSenha("nova");
        dto.setConfirmarNovaSenha("nova");

        assertEquals("antiga", dto.getSenhaAtual());
        assertEquals("nova", dto.getNovaSenha());
        assertEquals("nova", dto.getConfirmarNovaSenha());
    }

    @Test
    void produtoResponseDTO_GettersESetters() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(1L);
        dto.setNome("Ração");
        dto.setDescricao("Ração premium");
        dto.setUnidadeMedida("kg");

        assertEquals(1L, dto.getId());
        assertEquals("Ração", dto.getNome());
        assertEquals("Ração premium", dto.getDescricao());
        assertEquals("kg", dto.getUnidadeMedida());
    }

    @Test
    void cadastrarClienteDTO_GettersESetters() {
        CadastrarClienteDTO dto = new CadastrarClienteDTO();
        dto.setNome("João Silva");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setTelefone("11999998888");
        dto.setEmail("joao@email.com");
        dto.setSenha("Senha@123");

        assertEquals("João Silva", dto.getNome());
        assertEquals("12345678901", dto.getCpf());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDataNascimento());
        assertEquals("11999998888", dto.getTelefone());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("Senha@123", dto.getSenha());
    }
}
