package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private InternacaoRepository internacaoRepository;

    private String adminToken;
    private Animal testAnimal;
    private Internacao testInternacao;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN).orElseThrow();
        Cliente seededCliente = (Cliente) clienteUser.getPessoa();

        testAnimal = new Animal();
        testAnimal.setNome("Bolinha");
        testAnimal.setEspecie(EnumEspecie.CANINO);
        testAnimal.setPorte(EnumPorte.MEDIO);
        testAnimal.setRaca("SRD");
        testAnimal.setSexo(EnumSexo.MACHO);
        testAnimal.setPeso(15.0);
        testAnimal.setCastrado(false);
        testAnimal.setDataNascimento(LocalDate.of(2020, 3, 1));
        testAnimal.setCliente(seededCliente);
        testAnimal = animalRepository.save(testAnimal);

        testInternacao = new Internacao();
        testInternacao.setAnimal(testAnimal);
        testInternacao.setDataEntrada(LocalDateTime.now().minusDays(1));
        testInternacao = internacaoRepository.save(testInternacao);
    }

    @Test
    void criarInternacao_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        mockMvc.perform(post("/api/internacoes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarInternacao_ComDadosValidos_DeveRetornar201Created() throws Exception {
        Animal outroAnimal = new Animal();
        outroAnimal.setNome("Mel");
        outroAnimal.setEspecie(EnumEspecie.FELINO);
        outroAnimal.setPorte(EnumPorte.PEQUENO);
        outroAnimal.setRaca("SRD");
        outroAnimal.setSexo(EnumSexo.FEMEA);
        outroAnimal.setPeso(4.0);
        outroAnimal.setCastrado(true);
        outroAnimal.setDataNascimento(LocalDate.of(2021, 7, 15));
        outroAnimal.setCliente(testAnimal.getCliente());
        outroAnimal = animalRepository.save(outroAnimal);

        String jsonRequest = String.format("""
                {
                    "animalId": %d,
                    "dataEntrada": "%s"
                }
                """, outroAnimal.getId(), LocalDateTime.now().toString());

        mockMvc.perform(post("/api/internacoes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.animalId").value(outroAnimal.getId()))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    void criarInternacao_ComAnimalInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = """
                {
                    "animalId": 999999,
                    "dataEntrada": "2026-06-25T08:00:00"
                }
                """;

        mockMvc.perform(post("/api/internacoes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarTodas_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/internacoes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id == " + testInternacao.getId() + ")]").exists());
    }

    @Test
    void buscarInternacaoPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/internacoes/" + testInternacao.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInternacao.getId()))
                .andExpect(jsonPath("$.animalId").value(testAnimal.getId()))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    void buscarInternacaoPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/internacoes/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarInternacaoAtivaPorAnimal_ComAnimalComInternacaoAtiva_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/internacoes/animal/" + testAnimal.getId() + "/ativa")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInternacao.getId()))
                .andExpect(jsonPath("$.animalId").value(testAnimal.getId()))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    void buscarInternacaoAtivaPorAnimal_ComAnimalSemInternacaoAtiva_DeveRetornar404NotFound() throws Exception {
        Animal animalSemInternacao = new Animal();
        animalSemInternacao.setNome("Livre");
        animalSemInternacao.setEspecie(EnumEspecie.CANINO);
        animalSemInternacao.setPorte(EnumPorte.PEQUENO);
        animalSemInternacao.setRaca("SRD");
        animalSemInternacao.setSexo(EnumSexo.FEMEA);
        animalSemInternacao.setPeso(5.0);
        animalSemInternacao.setCastrado(true);
        animalSemInternacao.setDataNascimento(LocalDate.of(2022, 1, 1));
        animalSemInternacao.setCliente(testAnimal.getCliente());
        animalSemInternacao = animalRepository.save(animalSemInternacao);

        mockMvc.perform(get("/api/internacoes/animal/" + animalSemInternacao.getId() + "/ativa")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void darAlta_ComInternacaoExistente_DeveRetornar200ComStatusAlta() throws Exception {
        mockMvc.perform(post("/api/internacoes/" + testInternacao.getId() + "/alta")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInternacao.getId()))
                .andExpect(jsonPath("$.status").value("ALTA"))
                .andExpect(jsonPath("$.dataSaida").exists());
    }

    @Test
    void darAlta_Inexistente_DeveRetornar404Ou400() throws Exception {
        mockMvc.perform(post("/api/internacoes/999999/alta")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void atualizarInternacao_ComDadosValidos_DeveRetornar200Ok() throws Exception {
        String jsonRequest = String.format("""
                {
                    "animalId": %d,
                    "dataEntrada": "2026-06-20T10:00:00"
                }
                """, testAnimal.getId());

        mockMvc.perform(put("/api/internacoes/" + testInternacao.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInternacao.getId()))
                .andExpect(jsonPath("$.animalId").value(testAnimal.getId()));
    }

    @Test
    void atualizarInternacao_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "animalId": %d,
                    "dataEntrada": "2026-06-20T10:00:00"
                }
                """, testAnimal.getId());

        mockMvc.perform(put("/api/internacoes/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarInternacao_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        Internacao internacaoParaDeletar = new Internacao();
        internacaoParaDeletar.setAnimal(testAnimal);
        internacaoParaDeletar.setDataEntrada(LocalDateTime.now().minusHours(2));
        internacaoParaDeletar = internacaoRepository.save(internacaoParaDeletar);
        Long id = internacaoParaDeletar.getId();

        mockMvc.perform(delete("/api/internacoes/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(internacaoRepository.findById(id).isPresent());
    }

    @Test
    void deletarInternacao_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/internacoes/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
