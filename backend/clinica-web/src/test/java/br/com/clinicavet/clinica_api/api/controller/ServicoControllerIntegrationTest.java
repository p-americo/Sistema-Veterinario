package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Servico;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import br.com.clinicavet.clinica_api.domain.repository.ServicoRepository;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ServicoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    private String adminToken;
    private Funcionario seededVeterinario;
    private Servico testServico;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);
        seededVeterinario = (Funcionario) adminUser.getPessoa();

        Servico servico = new Servico();
        servico.setTipo(EnumServico.CONSULTA);
        servico.setVeterinario(seededVeterinario);
        servico.setValor(new BigDecimal("150.00"));
        testServico = servicoRepository.save(servico);
    }

    @Test
    void cadastrarServico_ComDadosValidos_DeveRetornar201Created() throws Exception {
        String jsonRequest = String.format("""
                {
                    "tipo": "CIRURGIA",
                    "valor": 850.00,
                    "veterinarioId": %d
                }
                """, seededVeterinario.getId());

        mockMvc.perform(post("/api/servicos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipo").value("CIRURGIA"))
                .andExpect(jsonPath("$.valor").value(850.00))
                .andExpect(jsonPath("$.nomeVeterinario").value(seededVeterinario.getNome()));
    }

    @Test
    void listarTodosServicos_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/servicos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void buscarServicoPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/servicos/" + testServico.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testServico.getId()))
                .andExpect(jsonPath("$.tipo").value("CONSULTA"))
                .andExpect(jsonPath("$.veterinarioId").value(seededVeterinario.getId()));
    }

    @Test
    void buscarServicoPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/servicos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarServico_ComDadosValidos_DeveRetornar200Ok() throws Exception {
        String jsonRequest = String.format("""
                {
                    "tipo": "VACINACAO",
                    "valor": 80.00,
                    "veterinarioId": %d
                }
                """, seededVeterinario.getId());

        mockMvc.perform(put("/api/servicos/" + testServico.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testServico.getId()))
                .andExpect(jsonPath("$.tipo").value("VACINACAO"))
                .andExpect(jsonPath("$.valor").value(80.00));
    }

    @Test
    void atualizarServico_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "tipo": "VACINACAO",
                    "valor": 80.00,
                    "veterinarioId": %d
                }
                """, seededVeterinario.getId());

        mockMvc.perform(put("/api/servicos/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarServico_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        Servico servicoParaDeletar = new Servico();
        servicoParaDeletar.setTipo(EnumServico.BANHO_E_TOSA);
        servicoParaDeletar.setVeterinario(seededVeterinario);
        servicoParaDeletar.setValor(new BigDecimal("60.00"));
        servicoParaDeletar = servicoRepository.save(servicoParaDeletar);
        Long id = servicoParaDeletar.getId();

        mockMvc.perform(delete("/api/servicos/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(servicoRepository.findById(id).isPresent());
    }

    @Test
    void deletarServico_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/servicos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
