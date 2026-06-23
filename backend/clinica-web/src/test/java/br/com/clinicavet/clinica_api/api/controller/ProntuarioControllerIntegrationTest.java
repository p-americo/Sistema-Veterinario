package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.*;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.repository.*;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ProntuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    private String adminToken;
    private Cliente seededCliente;
    private Animal testAnimal;
    private Prontuario testProntuario;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin("12345678900")
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin("11111111111")
                .orElseThrow(() -> new IllegalStateException("Cliente do DataSeeder não encontrado"));
        seededCliente = (Cliente) clienteUser.getPessoa();

        testAnimal = criarAnimal("Rex");

        Prontuario prontuario = new Prontuario();
        prontuario.setAnimal(testAnimal);
        prontuario.setAlergiasConhecidas("Nenhuma conhecida");
        prontuario.setCondicoesPreexistentes("Nenhuma");
        testProntuario = prontuarioRepository.save(prontuario);
    }

    @Test
    void criarProntuario_SemToken_DeveRetornar401Unauthorized() throws Exception {
        String jsonRequest = """
                {
                    "animalId": 1
                }
                """;

        mockMvc.perform(post("/api/prontuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criarProntuario_ComDadosValidos_DeveCriarERetornar201Created() throws Exception {
        Animal animalSemProntuario = criarAnimal("Bidu");

        String jsonRequest = String.format("""
                {
                    "animalId": %d,
                    "alergiasConhecidas": "Alergia a penicilina",
                    "condicoesPreexistentes": "Displasia coxofemoral"
                }
                """, animalSemProntuario.getId());

        mockMvc.perform(post("/api/prontuarios")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.animalId").value(animalSemProntuario.getId()))
                .andExpect(jsonPath("$.nomeAnimal").value("Bidu"))
                .andExpect(jsonPath("$.alergiasConhecidas").value("Alergia a penicilina"))
                .andExpect(jsonPath("$.condicoesPreexistentes").value("Displasia coxofemoral"));
    }

    @Test
    void criarProntuario_ComAnimalInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = """
                {
                    "animalId": 999999
                }
                """;

        mockMvc.perform(post("/api/prontuarios")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarProntuario_ComAnimalQueJaPossuiProntuario_DeveRetornar409Conflict() throws Exception {
        String jsonRequest = String.format("""
                {
                    "animalId": %d
                }
                """, testAnimal.getId());

        mockMvc.perform(post("/api/prontuarios")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void criarProntuario_SemAnimalId_DeveRetornar400BadRequest() throws Exception {
        mockMvc.perform(post("/api/prontuarios")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTodosProntuarios_ComToken_DeveRetornarPaginaComProntuarioCriado() throws Exception {
        mockMvc.perform(get("/api/prontuarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == " + testProntuario.getId() + ")]").exists());
    }

    @Test
    void buscarProntuarioPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/prontuarios/{id}", testProntuario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProntuario.getId()))
                .andExpect(jsonPath("$.animalId").value(testAnimal.getId()));
    }

    @Test
    void buscarProntuarioPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/prontuarios/{id}", 999999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarProntuarioPorIdComRegistros_ComIdExistente_DeveRetornar200ComListaDeRegistros() throws Exception {
        mockMvc.perform(get("/api/prontuarios/{id}/completo", testProntuario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProntuario.getId()))
                .andExpect(jsonPath("$.registros").isArray());
    }

    @Test
    void buscarProntuarioPorIdComRegistros_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/prontuarios/{id}/completo", 999999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarProntuarioPorAnimalId_ComAnimalComProntuario_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/prontuarios/animal/{animalId}", testAnimal.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProntuario.getId()))
                .andExpect(jsonPath("$.animalId").value(testAnimal.getId()));
    }

    @Test
    void buscarProntuarioPorAnimalId_ComAnimalSemProntuario_DeveRetornar404NotFound() throws Exception {
        Animal animalSemProntuario = criarAnimal("Bidu");

        mockMvc.perform(get("/api/prontuarios/animal/{animalId}", animalSemProntuario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarProntuario_ComDadosValidos_DeveAtualizarERetornar200Ok() throws Exception {
        String jsonRequest = String.format("""
                {
                    "animalId": %d,
                    "alergiasConhecidas": "Alergia a sulfa",
                    "condicoesPreexistentes": "Insuficiência renal leve"
                }
                """, testAnimal.getId());

        mockMvc.perform(put("/api/prontuarios/{id}", testProntuario.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alergiasConhecidas").value("Alergia a sulfa"))
                .andExpect(jsonPath("$.condicoesPreexistentes").value("Insuficiência renal leve"));
    }

    @Test
    void atualizarProntuario_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "animalId": %d,
                    "alergiasConhecidas": "Alergia a sulfa"
                }
                """, testAnimal.getId());

        mockMvc.perform(put("/api/prontuarios/{id}", 999999)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarProntuario_ComIdExistente_DeveRetornar204EApagarProntuario() throws Exception {
        Animal animalSemProntuario = criarAnimal("Bidu");
        Prontuario prontuarioParaDeletar = new Prontuario();
        prontuarioParaDeletar.setAnimal(animalSemProntuario);
        prontuarioParaDeletar = prontuarioRepository.save(prontuarioParaDeletar);
        Long prontuarioId = prontuarioParaDeletar.getId();

        mockMvc.perform(delete("/api/prontuarios/{id}", prontuarioId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertThat(prontuarioRepository.existsById(prontuarioId)).isFalse();
    }

    @Test
    void deletarProntuario_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/prontuarios/{id}", 999999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    private Animal criarAnimal(String nome) {
        Animal animal = new Animal();
        animal.setNome(nome);
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.MEDIO);
        animal.setRaca("Labrador");
        animal.setSexo(EnumSexo.MACHO);
        animal.setPeso(20.0);
        animal.setCastrado(false);
        animal.setDataNascimento(LocalDate.of(2021, 6, 1));
        animal.setCliente(seededCliente);
        return animalRepository.save(animal);
    }
}
