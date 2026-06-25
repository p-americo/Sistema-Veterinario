package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class DiariaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private InternacaoRepository internacaoRepository;

    private String adminToken;
    private Internacao testInternacao;
    private DiariaInternacao testDiaria;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN).orElseThrow();
        Cliente seededCliente = (Cliente) clienteUser.getPessoa();

        Animal animal = new Animal();
        animal.setNome("Bidu");
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.PEQUENO);
        animal.setRaca("Poodle");
        animal.setSexo(EnumSexo.MACHO);
        animal.setPeso(7.5);
        animal.setCastrado(true);
        animal.setDataNascimento(LocalDate.of(2018, 3, 20));
        animal.setCliente(seededCliente);
        Animal savedAnimal = animalRepository.save(animal);

        Internacao internacao = new Internacao();
        internacao.setAnimal(savedAnimal);
        internacao.setDataEntrada(LocalDateTime.now().minusDays(2));
        testInternacao = internacaoRepository.save(internacao);

        DiariaInternacao diaria = new DiariaInternacao();
        diaria.setDataHora(LocalDateTime.now().minusDays(1));
        diaria.setPesoNoDia(new BigDecimal("7.4"));
        diaria.setObservacoesClinicas("Animal estável, comendo bem.");
        diaria.setDiagnostico("Recuperação pós-cirúrgica");
        
        testInternacao.adicionarDiaria(diaria);
        internacaoRepository.save(testInternacao);
        
        testDiaria = testInternacao.getDiarias().iterator().next();
    }

    @Test
    void criarDiaria_ComDadosValidos_DeveRetornar201Created() throws Exception {
        String jsonRequest = String.format("""
                {
                    "internacaoId": %d,
                    "dataHora": "2026-06-25T08:00:00",
                    "pesoNoDia": 7.3,
                    "observacoesClinicas": "Melhora progressiva, febre diminuiu.",
                    "diagnostico": "Recuperação cirúrgica estável"
                }
                """, testInternacao.getId());

        mockMvc.perform(post("/api/diarias-internacao")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.observacoesClinicas").value("Melhora progressiva, febre diminuiu."))
                .andExpect(jsonPath("$.diagnostico").value("Recuperação cirúrgica estável"));
    }

    @Test
    void criarDiaria_ComInternacaoInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = """
                {
                    "internacaoId": 999999,
                    "dataHora": "2026-06-25T08:00:00",
                    "pesoNoDia": 7.3,
                    "observacoesClinicas": "Teste",
                    "diagnostico": "Teste"
                }
                """;

        mockMvc.perform(post("/api/diarias-internacao")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarDiaria_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        // Campos obrigatórios nulos
        String jsonRequest = """
                {
                    "internacaoId": null,
                    "dataHora": null
                }
                """;

        mockMvc.perform(post("/api/diarias-internacao")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarDiaria_ComDadosValidos_DeveRetornarDiariaAtualizada200Ok() throws Exception {
        String jsonRequest = String.format("""
                {
                    "internacaoId": %d,
                    "dataHora": "2026-06-25T09:00:00",
                    "pesoNoDia": 7.6,
                    "observacoesClinicas": "Alterado: apresentou episódios de vômito.",
                    "diagnostico": "Gastrite secundária"
                }
                """, testInternacao.getId());

        mockMvc.perform(put("/api/diarias-internacao/" + testDiaria.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDiaria.getId()))
                .andExpect(jsonPath("$.pesoNoDia").value(7.6))
                .andExpect(jsonPath("$.observacoesClinicas").value("Alterado: apresentou episódios de vômito."));
    }

    @Test
    void atualizarDiaria_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "internacaoId": %d,
                    "dataHora": "2026-06-25T09:00:00",
                    "pesoNoDia": 7.6,
                    "observacoesClinicas": "Teste",
                    "diagnostico": "Teste"
                }
                """, testInternacao.getId());

        mockMvc.perform(put("/api/diarias-internacao/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarDiariaPorId_ComIdExistente_DeveRetornarDiaria200Ok() throws Exception {
        mockMvc.perform(get("/api/diarias-internacao/" + testDiaria.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDiaria.getId()))
                .andExpect(jsonPath("$.observacoesClinicas").value("Animal estável, comendo bem."))
                .andExpect(jsonPath("$.diagnostico").value("Recuperação pós-cirúrgica"));
    }

    @Test
    void buscarDiariaPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/diarias-internacao/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarTodasDiarias_DeveRetornarListaPaginada200Ok() throws Exception {
        mockMvc.perform(get("/api/diarias-internacao")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(testDiaria.getId()));
    }

    @Test
    void listarDiariasPorInternacao_ComIdExistente_DeveRetornarLista200Ok() throws Exception {
        mockMvc.perform(get("/api/diarias-internacao/internacao/" + testInternacao.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testDiaria.getId()));
    }

    @Test
    void deletarDiaria_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/diarias-internacao/" + testDiaria.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(internacaoRepository.findDiariaById(testDiaria.getId()).isPresent());
    }

    @Test
    void deletarDiaria_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/diarias-internacao/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
