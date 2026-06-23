package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class EnumControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin("12345678900")
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);
    }

    @Test
    void getEspecies_SemToken_DeveRetornar401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/enums/especies"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEspecies_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/especies")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0]").value("CANINO"))
                .andExpect(jsonPath("$[7]").value("OUTRO"));
    }

    @Test
    void getPortes_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/portes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("PEQUENO"))
                .andExpect(jsonPath("$[1]").value("MEDIO"))
                .andExpect(jsonPath("$[2]").value("GRANDE"));
    }

    @Test
    void getSexos_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/sexos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("MACHO"))
                .andExpect(jsonPath("$[1]").value("FEMEA"));
    }

    @Test
    void getAgendamentoStatus_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/agendamento-status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0]").value("AGENDADO"))
                .andExpect(jsonPath("$[3]").value("CONFIRMADO"));
    }

    @Test
    void getServicoTipos_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/servico-tipos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0]").value("CONSULTA"))
                .andExpect(jsonPath("$[5]").value("INTERNACAO"));
    }

    @Test
    void getMedicamentoCategorias_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/medicamento-categorias")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0]").value("ANTIBIOTICO"))
                .andExpect(jsonPath("$[7]").value("OUTROS"));
    }

    @Test
    void getMedicamentoVias_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/medicamento-vias")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0]").value("ORAL"))
                .andExpect(jsonPath("$[5]").value("INALATORIO"));
    }

    @Test
    void getInternacaoStatus_ComToken_DeveRetornarTodosOsValores() throws Exception {
        mockMvc.perform(get("/api/enums/internacao-status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("ATIVA"))
                .andExpect(jsonPath("$[2]").value("CANCELADA"));
    }
}
