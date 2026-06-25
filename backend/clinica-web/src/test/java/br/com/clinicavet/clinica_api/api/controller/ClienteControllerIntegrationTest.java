package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
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
public class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private String adminToken;
    private String clienteToken;
    private Cliente seededCliente;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Cliente do DataSeeder não encontrado"));
        clienteToken = tokenService.gerarToken(clienteUser);
        seededCliente = (Cliente) clienteUser.getPessoa();
    }

    @Test
    void criarCliente_ComDadosValidos_DeveRetornar201Created() throws Exception {
        String jsonRequest = """
                {
                    "nome": "João da Silva",
                    "cpf": "98765432100",
                    "dataNascimento": "1985-04-12",
                    "telefone": "11988887777",
                    "email": "joao.silva@email.com",
                    "senha": "SenhaValida@123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"));
    }

    @Test
    void criarCliente_ComCpfDuplicado_DeveRetornar409Conflict() throws Exception {
        String jsonRequest = """
                {
                    "nome": "João da Silva Duplicado",
                    "cpf": "11111111111",
                    "dataNascimento": "1985-04-12",
                    "telefone": "11988887777",
                    "email": "joao.duplicado@email.com",
                    "senha": "SenhaValida@123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void criarCliente_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = """
                {
                    "nome": "João",
                    "cpf": "123",
                    "dataNascimento": "2030-01-01",
                    "telefone": "12",
                    "email": "email_invalido",
                    "senha": "123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodosClientes_DeveRetornarListaPaginada200Ok() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].cpf").value(seededCliente.getCpf()));
    }

    @Test
    void buscarClientePorId_ComIdExistente_DeveRetornarCliente200Ok() throws Exception {
        mockMvc.perform(get("/api/clientes/" + seededCliente.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededCliente.getId()))
                .andExpect(jsonPath("$.nome").value(seededCliente.getNome()))
                .andExpect(jsonPath("$.cpf").value(seededCliente.getCpf()));
    }

    @Test
    void buscarClientePorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/clientes/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarClientePorNome_ComNomeParcial_DeveRetornarLista200Ok() throws Exception {
        mockMvc.perform(get("/api/clientes/nome/Teste")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value(seededCliente.getNome()));
    }

    @Test
    void obterClienteLogado_ComTokenValido_DeveRetornarClienteLogado200Ok() throws Exception {
        mockMvc.perform(get("/api/clientes/me")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(seededCliente.getCpf()))
                .andExpect(jsonPath("$.nome").value(seededCliente.getNome()));
    }

    @Test
    void obterClienteLogado_SemToken_DeveRetornar401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/clientes/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void atualizarCliente_ComDadosValidos_DeveRetornarClienteAtualizado200Ok() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Cliente de Teste Alterado",
                    "telefone": "11999998888"
                }
                """;

        mockMvc.perform(put("/api/clientes/" + seededCliente.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededCliente.getId()))
                .andExpect(jsonPath("$.nome").value("Cliente de Teste Alterado"))
                .andExpect(jsonPath("$.telefone").value("11999998888"));
    }

    @Test
    void atualizarCliente_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Cliente Inexistente",
                    "telefone": "11999998888"
                }
                """;

        mockMvc.perform(put("/api/clientes/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarCliente_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/clientes/" + seededCliente.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(clienteRepository.findById(seededCliente.getId()).isPresent());
    }

    @Test
    void deletarCliente_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/clientes/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
