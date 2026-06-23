package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.*;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.repository.*;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class RegistroProntuarioControllerIntegrationTest {

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

    @Autowired
    private RegistroProntuarioRepository registroProntuarioRepository;

    @Autowired
    private InternacaoRepository internacaoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private String adminToken;
    private Cliente seededCliente;
    private Funcionario seededVeterinario;
    private Prontuario testProntuario;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin("12345678900")
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);
        seededVeterinario = (Funcionario) adminUser.getPessoa();

        Usuario clienteUser = usuarioRepository.findByLogin("11111111111")
                .orElseThrow(() -> new IllegalStateException("Cliente do DataSeeder não encontrado"));
        seededCliente = (Cliente) clienteUser.getPessoa();

        Animal animal = new Animal();
        animal.setNome("Rex");
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.MEDIO);
        animal.setRaca("Labrador");
        animal.setSexo(EnumSexo.MACHO);
        animal.setPeso(20.0);
        animal.setCastrado(false);
        animal.setDataNascimento(LocalDate.of(2021, 6, 1));
        animal.setCliente(seededCliente);
        Animal animalSalvo = animalRepository.save(animal);

        Prontuario prontuario = new Prontuario();
        prontuario.setAnimal(animalSalvo);
        prontuario.setAlergiasConhecidas("Nenhuma conhecida");
        testProntuario = prontuarioRepository.save(prontuario);
    }

    @Test
    void criarRegistro_SemToken_DeveRetornar401Unauthorized() throws Exception {
        String jsonRequest = """
                {
                    "prontuarioId": 1,
                    "veterinarioResponsavelId": 1,
                    "dataHora": "2026-06-22T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/registros-prontuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criarRegistro_ComDadosValidos_DeveCriarERetornar201Created() throws Exception {
        String jsonRequest = String.format("""
                {
                    "prontuarioId": %d,
                    "veterinarioResponsavelId": %d,
                    "dataHora": "2026-06-22T10:00:00",
                    "observacoesClinicas": "Animal apresentou bom estado geral",
                    "diagnostico": "Saudável"
                }
                """, testProntuario.getId(), seededVeterinario.getId());

        mockMvc.perform(post("/api/registros-prontuario")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.prontuarioId").value(testProntuario.getId()))
                .andExpect(jsonPath("$.veterinarioResponsavelId").value(seededVeterinario.getId()))
                .andExpect(jsonPath("$.nomeVeterinario").value(seededVeterinario.getNome()))
                .andExpect(jsonPath("$.observacoesClinicas").value("Animal apresentou bom estado geral"))
                .andExpect(jsonPath("$.diagnostico").value("Saudável"));
    }

    @Test
    void criarRegistro_ComProntuarioInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "prontuarioId": 999999,
                    "veterinarioResponsavelId": %d,
                    "dataHora": "2026-06-22T10:00:00"
                }
                """, seededVeterinario.getId());

        mockMvc.perform(post("/api/registros-prontuario")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarRegistro_SemCamposObrigatorios_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = "{}";

        mockMvc.perform(post("/api/registros-prontuario")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarRegistroPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();

        mockMvc.perform(get("/api/registros-prontuario/{id}", registro.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registro.getId()));
    }

    @Test
    void buscarRegistroPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/registros-prontuario/{id}", 999999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarRegistrosPorProntuarioId_DeveRetornarListaComRegistroCriado() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();

        mockMvc.perform(get("/api/registros-prontuario/prontuario/{prontuarioId}", testProntuario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(registro.getId()));
    }

    @Test
    void buscarTodosRegistros_ComToken_DeveRetornarPaginaComRegistroCriado() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();

        mockMvc.perform(get("/api/registros-prontuario")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == " + registro.getId() + ")]").exists());
    }

    @Test
    void buscarRegistroPorIdComMedicamentos_ComIdExistente_DeveRetornar200ComListaVazia() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();

        mockMvc.perform(get("/api/registros-prontuario/{id}/medicamentos", registro.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registro.getId()))
                .andExpect(jsonPath("$.medicamentosAdministrados").isArray())
                .andExpect(jsonPath("$.medicamentosAdministrados.length()").value(0));
    }

    @Test
    void buscarRegistroPorIdComMedicamentos_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/registros-prontuario/{id}/medicamentos", 999999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarRegistrosPorVeterinarioId_DeveRetornarListaComRegistroCriado() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();

        mockMvc.perform(get("/api/registros-prontuario/veterinario/{veterinarioId}", seededVeterinario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + registro.getId() + ")]").exists());
    }

    @Test
    void buscarRegistrosPorInternacaoId_DeveRetornarListaComRegistroCriado() throws Exception {
        Internacao internacao = new Internacao();
        internacao.setAnimal(testProntuario.getAnimal());
        internacao.setDataEntrada(LocalDateTime.now());
        Internacao internacaoSalva = internacaoRepository.save(internacao);

        RegistroProntuario registro = criarRegistroComInternacao(internacaoSalva);

        mockMvc.perform(get("/api/registros-prontuario/internacao/{internacaoId}", internacaoSalva.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(registro.getId()))
                .andExpect(jsonPath("$[0].internacaoId").value(internacaoSalva.getId()));
    }

    @Test
    void buscarRegistrosPorPeriodo_DentroDoIntervalo_DeveRetornarRegistroCriado() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();
        LocalDateTime inicio = registro.getDataHora().minusHours(1);
        LocalDateTime fim = registro.getDataHora().plusHours(1);

        mockMvc.perform(get("/api/registros-prontuario/periodo")
                        .param("inicio", inicio.toString())
                        .param("fim", fim.toString())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + registro.getId() + ")]").exists());
    }

    @Test
    void buscarRegistrosPorPeriodo_ForaDoIntervalo_DeveRetornarListaVazia() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();
        LocalDateTime inicio = registro.getDataHora().plusDays(10);
        LocalDateTime fim = registro.getDataHora().plusDays(20);

        mockMvc.perform(get("/api/registros-prontuario/periodo")
                        .param("inicio", inicio.toString())
                        .param("fim", fim.toString())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + registro.getId() + ")]").doesNotExist());
    }

    @Test
    void atualizarRegistro_ComDadosValidos_DeveAtualizarERetornar200Ok() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();

        String jsonRequest = String.format("""
                {
                    "prontuarioId": %d,
                    "veterinarioResponsavelId": %d,
                    "dataHora": "2026-06-23T11:00:00",
                    "observacoesClinicas": "Reavaliação após tratamento",
                    "diagnostico": "Em recuperação"
                }
                """, testProntuario.getId(), seededVeterinario.getId());

        mockMvc.perform(put("/api/registros-prontuario/{id}", registro.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnostico").value("Em recuperação"))
                .andExpect(jsonPath("$.observacoesClinicas").value("Reavaliação após tratamento"));
    }

    @Test
    void atualizarRegistro_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "prontuarioId": %d,
                    "veterinarioResponsavelId": %d,
                    "dataHora": "2026-06-23T11:00:00"
                }
                """, testProntuario.getId(), seededVeterinario.getId());

        mockMvc.perform(put("/api/registros-prontuario/{id}", 999999)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarRegistro_ComIdExistente_DeveRetornar204EApagarRegistro() throws Exception {
        RegistroProntuario registro = criarRegistroPersistido();
        Long registroId = registro.getId();

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(delete("/api/registros-prontuario/{id}", registroId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        entityManager.flush();
        assertThat(registroProntuarioRepository.existsById(registroId)).isFalse();
    }

    @Test
    void deletarRegistro_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/registros-prontuario/{id}", 999999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    private RegistroProntuario criarRegistroPersistido() {
        RegistroProntuario registro = new RegistroProntuario();
        registro.setDataHora(LocalDateTime.now());
        registro.setObservacoesClinicas("Observação inicial");
        registro.setDiagnostico("Diagnóstico inicial");
        registro.associarAoProntuario(testProntuario);
        registro.setVeterinarioResponsavel(seededVeterinario);
        return registroProntuarioRepository.save(registro);
    }

    private RegistroProntuario criarRegistroComInternacao(Internacao internacao) {
        RegistroProntuario registro = new RegistroProntuario();
        registro.setDataHora(LocalDateTime.now());
        registro.setObservacoesClinicas("Observação durante internação");
        registro.associarAoProntuario(testProntuario);
        registro.setVeterinarioResponsavel(seededVeterinario);
        registro.setInternacao(internacao);
        return registroProntuarioRepository.save(registro);
    }
}
