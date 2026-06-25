package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
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
class CargoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CargoRepository cargoRepository;

    private String adminToken;
    private String clienteToken;
    private Cargo cargoVeterinario;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN).orElseThrow();
        clienteToken = tokenService.gerarToken(clienteUser);

        cargoVeterinario = cargoRepository.findByCargo(EnumCargo.VETERINARIO).orElseThrow();
    }

    @Test
    void criarCargo_ComDadosValidos_DeveRetornar201Created() throws Exception {
        String jsonRequest = """
                {
                    "cargo": "ORTOPEDISTA",
                    "salario": 9000.00
                }
                """;

        mockMvc.perform(post("/api/cargos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cargo").value("ORTOPEDISTA"))
                .andExpect(jsonPath("$.salario").value(9000.00));
    }

    @Test
    void criarCargo_ComCargoJaExistente_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = """
                {
                    "cargo": "VETERINARIO",
                    "salario": 9500.00
                }
                """;

        mockMvc.perform(post("/api/cargos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarCargo_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = """
                {
                    "salario": -100.00
                }
                """;

        mockMvc.perform(post("/api/cargos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarCargo_SemToken_DeveRetornar401Unauthorized() throws Exception {
        String jsonRequest = """
                {
                    "cargo": "ORTOPEDISTA",
                    "salario": 9000.00
                }
                """;

        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criarCargo_ComTokenDeCliente_DeveRetornar403Forbidden() throws Exception {
        String jsonRequest = """
                {
                    "cargo": "ORTOPEDISTA",
                    "salario": 9000.00
                }
                """;

        mockMvc.perform(post("/api/cargos")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarCargos_DeveRetornarListaPaginada200Ok() throws Exception {
        mockMvc.perform(get("/api/cargos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.cargo == 'VETERINARIO')]").exists())
                .andExpect(jsonPath("$.content[?(@.cargo == 'RECEPCIONISTA')]").exists());
    }

    @Test
    void buscarCargoPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/cargos/" + cargoVeterinario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cargoVeterinario.getId()))
                .andExpect(jsonPath("$.cargo").value("VETERINARIO"))
                .andExpect(jsonPath("$.salario").value(8000.00));
    }

    @Test
    void buscarCargoPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/cargos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarCargo_ComDadosValidos_DeveRetornar200Ok() throws Exception {
        Cargo cargoParaAtualizar = new Cargo();
        cargoParaAtualizar.setCargo(EnumCargo.ORTOPEDISTA);
        cargoParaAtualizar.setSalario(new BigDecimal("9000.00"));
        cargoParaAtualizar = cargoRepository.save(cargoParaAtualizar);

        String jsonRequest = """
                {
                    "cargo": "ORTOPEDISTA",
                    "salario": 9500.00
                }
                """;

        mockMvc.perform(put("/api/cargos/" + cargoParaAtualizar.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cargoParaAtualizar.getId()))
                .andExpect(jsonPath("$.cargo").value("ORTOPEDISTA"))
                .andExpect(jsonPath("$.salario").value(9500.00));
    }

    @Test
    void atualizarCargo_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = """
                {
                    "cargo": "ORTOPEDISTA",
                    "salario": 9500.00
                }
                """;

        mockMvc.perform(put("/api/cargos/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarCargo_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        Cargo cargoParaDeletar = new Cargo();
        cargoParaDeletar.setCargo(EnumCargo.ANESTEASIOLOGISTA);
        cargoParaDeletar.setSalario(new BigDecimal("7000.00"));
        cargoParaDeletar = cargoRepository.save(cargoParaDeletar);
        Long id = cargoParaDeletar.getId();

        mockMvc.perform(delete("/api/cargos/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(cargoRepository.findById(id).isPresent());
    }

    @Test
    void deletarCargo_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/cargos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
