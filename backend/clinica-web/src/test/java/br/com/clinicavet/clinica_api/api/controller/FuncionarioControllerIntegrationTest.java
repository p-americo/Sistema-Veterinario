package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import br.com.clinicavet.clinica_api.testsupport.DataSeederFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class FuncionarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String adminToken;
    private String clienteToken;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN).orElseThrow();
        clienteToken = tokenService.gerarToken(clienteUser);
    }

    @Test
    void criarFuncionario_ComoAdminEComDadosValidos_DeveRetornar201Created() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Dr. Carlos Eduardo",
                    "cpf": "12398765400",
                    "dataNascimento": "1988-05-15",
                    "telefone": "11988887777",
                    "email": "carlos.veterinario@clinicavet.com",
                    "cargoId": 1,
                    "crmv": "CRMV-SP12345",
                    "dataAdmissao": "2026-06-24",
                    "senha": "SenhaSegura123!"
                }
                """;

        mockMvc.perform(post("/api/funcionarios")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Dr. Carlos Eduardo"));
    }

    @Test
    void buscarTodos_DeveRetornarListaPaginada200Ok() throws Exception {
        mockMvc.perform(get("/api/funcionarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void listarVeterinarios_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/funcionarios/veterinarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorId_Inexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/funcionarios/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}