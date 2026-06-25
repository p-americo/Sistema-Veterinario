package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
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

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private String adminToken;
    private String clienteToken;
    private Funcionario seededFuncionario;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);
        seededFuncionario = (Funcionario) adminUser.getPessoa();

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
    void criarFuncionario_ComTokenDeCliente_DeveRetornar403Forbidden() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Funcionário Negado",
                    "cpf": "99988877766",
                    "dataNascimento": "1990-03-15",
                    "telefone": "11977776666",
                    "email": "negado@clinica.com",
                    "cargoId": 1,
                    "crmv": "CRMV-SP00001",
                    "dataAdmissao": "2026-01-01",
                    "senha": "SenhaSegura123!"
                }
                """;

        mockMvc.perform(post("/api/funcionarios")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
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
    void buscarPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/funcionarios/" + seededFuncionario.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededFuncionario.getId()))
                .andExpect(jsonPath("$.nome").value(seededFuncionario.getNome()))
                .andExpect(jsonPath("$.cpf").value(seededFuncionario.getCpf()));
    }

    @Test
    void buscarPorId_Inexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/funcionarios/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorNome_ComNomeParcial_DeveRetornarLista200Ok() throws Exception {
        mockMvc.perform(get("/api/funcionarios/nome/Administrador")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value(seededFuncionario.getNome()));
    }

    @Test
    void buscarPorNome_ComNomeInexistente_DeveRetornarListaVazia200Ok() throws Exception {
        mockMvc.perform(get("/api/funcionarios/nome/NomeQueNaoExiste")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void atualizarFuncionario_ComDadosValidos_DeveRetornar200Ok() throws Exception {
        Long cargoId = seededFuncionario.getCargo().getId();
        String jsonRequest = String.format("""
                {
                    "nome": "Administrador Atualizado",
                    "cpf": "12345678900",
                    "dataNascimento": "1985-05-20",
                    "telefone": "11988888888",
                    "email": "admin@clinica.com",
                    "dataAdmissao": "2024-01-01",
                    "crmv": "CRMV-SP99999",
                    "cargoId": %d
                }
                """, cargoId);

        mockMvc.perform(put("/api/funcionarios/" + seededFuncionario.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededFuncionario.getId()))
                .andExpect(jsonPath("$.nome").value("Administrador Atualizado"))
                .andExpect(jsonPath("$.telefone").value("11988888888"));
    }

    @Test
    void atualizarFuncionario_ComIdInexistente_DeveRetornar4xx() throws Exception {
        Long cargoId = seededFuncionario.getCargo().getId();
        String jsonRequest = String.format("""
                {
                    "nome": "Ninguém",
                    "cpf": "11122233344",
                    "dataNascimento": "1990-01-01",
                    "telefone": "11977777777",
                    "email": "ninguem@email.com",
                    "dataAdmissao": "2024-01-01",
                    "cargoId": %d
                }
                """, cargoId);

        mockMvc.perform(put("/api/funcionarios/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deletarFuncionario_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        Funcionario funcionarioParaDeletar = new Funcionario();
        funcionarioParaDeletar.setNome("Para Deletar");
        funcionarioParaDeletar.setCpf("55566677788");
        funcionarioParaDeletar.setDataNascimento(LocalDate.of(1992, 7, 20));
        funcionarioParaDeletar.setTelefone("11955554444");
        funcionarioParaDeletar.setEmail("para.deletar@clinica.com");
        funcionarioParaDeletar.setDataAdmissao(LocalDate.now());
        funcionarioParaDeletar.alterarCargo(seededFuncionario.getCargo(), "CRMV-DELETE-999");
        funcionarioParaDeletar = funcionarioRepository.save(funcionarioParaDeletar);
        Long id = funcionarioParaDeletar.getId();

        mockMvc.perform(delete("/api/funcionarios/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarFuncionario_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/funcionarios/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
