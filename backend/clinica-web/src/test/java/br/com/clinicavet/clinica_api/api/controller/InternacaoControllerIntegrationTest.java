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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class InternacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);
    }

    @Test
    void criarInternacao_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = "{}"; // Objeto vazio violará @Valid

        mockMvc.perform(post("/api/internacoes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodas_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/internacoes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void darAlta_Inexistent_DeveRetornar404Ou400() throws Exception {
        mockMvc.perform(post("/api/internacoes/999999/alta")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is4xxClientError());
    }
}