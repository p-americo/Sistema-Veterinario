package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.application.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;

@Tag(name = "Clientes", description = "Gerenciamento de clientes da clínica")
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo cliente", description = "Cadastra um cliente e gera automaticamente seu usuário correspondente de acesso síncronamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso.",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de dados (CPF ou E-mail já cadastrado).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody @Valid ClienteRequestDTO clienteRequestDTO, UriComponentsBuilder uriBuilder) {
        ClienteResponseDTO responseDTO = clienteService.criar(clienteRequestDTO);
        URI uri = uriBuilder.path("/api/clientes/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes de forma paginada", description = "Retorna uma página contendo os clientes cadastrados.")
    public ResponseEntity<Page<ClienteResponseDTO>> listarTodosClientes(@PageableDefault(size = 10) Pageable pageable) {
        Page<ClienteResponseDTO> listaClientes = clienteService.listarTodos(pageable);
        return ResponseEntity.ok(listaClientes);
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar clientes pelo nome", description = "Retorna uma lista de clientes cujo nome contém o termo pesquisado (case-insensitive).")
    public ResponseEntity<List<ClienteResponseDTO>> buscarClientePorNome(@PathVariable String nome) {
        List<ClienteResponseDTO> listaClientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(listaClientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os detalhes de um cliente específico com base no seu ID único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Long id) {
        ClienteResponseDTO responseDTO = clienteService.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do cliente logado", description = "Retorna os dados cadastrais do cliente associado ao token JWT autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ClienteResponseDTO> obterClienteLogado(@AuthenticationPrincipal UserDetails userDetails) {
        ClienteResponseDTO responseDTO = clienteService.buscarPorCpf(userDetails.getUsername());
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Atualiza os dados de um cliente existente",
            description = "Apenas os campos fornecidos no corpo da requisição serão atualizados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso.",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de dados (CPF ou E-mail já cadastrado por outro usuário).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id, @RequestBody @Valid ClienteUpdateDTO UpdateRequestDTO) {
        ClienteResponseDTO responseDTO = clienteService.atualizarCliente(id, UpdateRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um cliente por ID", description = "Remove um cliente do sistema com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
