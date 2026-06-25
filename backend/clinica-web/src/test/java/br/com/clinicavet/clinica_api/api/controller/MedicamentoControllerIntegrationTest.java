package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumViaMedicamento;
import br.com.clinicavet.clinica_api.domain.repository.MedicamentoRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class MedicamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    private String adminToken;
    private Medicamento testMedicamento;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);

        Produto produto = new Produto();
        produto.setNome("Dipirona Vet");
        produto.setDescricao("Analgésico geral");
        produto.inicializarEstoque(100);

        Medicamento medicamento = new Medicamento();
        medicamento.setProduto(produto);
        medicamento.setCategoria(EnumCategoriaMedicameno.ANALGESICO);
        medicamento.setViaAdministracao(EnumViaMedicamento.ORAL);
        medicamento.setDosagemPadrao("1 gota/kg");
        medicamento.setPrincipioAtivo("Dipirona");
        medicamento.setPrescricaoObrigatoria(false);

        testMedicamento = medicamentoRepository.save(medicamento);
    }

    @Test
    void criarMedicamento_ComDadosValidos_DeveRetornar201Created() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Amoxicilina 500mg",
                    "descricao": "Antibiótico de amplo espectro",
                    "quantidadeEstoque": 50,
                    "categoria": "ANTIBIOTICO",
                    "viaAdministracao": "ORAL",
                    "dosagemPadrao": "1 capsula a cada 12 horas",
                    "principioAtivo": "Amoxicilina",
                    "fabricante": "VetLabs",
                    "prescricaoObrigatoria": true
                }
                """;

        mockMvc.perform(post("/api/medicamentos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Amoxicilina 500mg"))
                .andExpect(jsonPath("$.categoria").value("ANTIBIOTICO"));
    }

    @Test
    void criarMedicamento_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        // Nome em branco e categoria nula
        String jsonRequest = """
                {
                    "nome": "",
                    "quantidadeEstoque": 50,
                    "categoria": null
                }
                """;

        mockMvc.perform(post("/api/medicamentos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodosMedicamentos_DeveRetornarListaPaginada200Ok() throws Exception {
        mockMvc.perform(get("/api/medicamentos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Dipirona Vet"));
    }

    @Test
    void buscarMedicamentoPorId_ComIdExistente_DeveRetornarMedicamento200Ok() throws Exception {
        mockMvc.perform(get("/api/medicamentos/" + testMedicamento.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMedicamento.getId()))
                .andExpect(jsonPath("$.nome").value("Dipirona Vet"))
                .andExpect(jsonPath("$.categoria").value("ANALGESICO"));
    }

    @Test
    void buscarMedicamentoPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/medicamentos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarMedicamento_ComDadosValidos_DeveRetornarMedicamentoAtualizado200Ok() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Dipirona Vet Gotas",
                    "descricao": "Analgésico rápido",
                    "quantidadeEstoque": 120,
                    "categoria": "ANALGESICO",
                    "viaAdministracao": "INJETAVEL",
                    "dosagemPadrao": "2 gotas/kg",
                    "principioAtivo": "Dipirona Monoidratada",
                    "fabricante": "Farmavet",
                    "prescricaoObrigatoria": false
                }
                """;

        mockMvc.perform(put("/api/medicamentos/" + testMedicamento.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMedicamento.getId()))
                .andExpect(jsonPath("$.nome").value("Dipirona Vet Gotas"))
                .andExpect(jsonPath("$.viaAdministracao").value("INJETAVEL"));
    }

    @Test
    void atualizarMedicamento_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Dipirona Inexistente",
                    "quantidadeEstoque": 10,
                    "categoria": "ANALGESICO"
                }
                """;

        mockMvc.perform(put("/api/medicamentos/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarMedicamento_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/medicamentos/" + testMedicamento.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(medicamentoRepository.findById(testMedicamento.getId()).isPresent());
    }

    @Test
    void deletarMedicamento_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/medicamentos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
