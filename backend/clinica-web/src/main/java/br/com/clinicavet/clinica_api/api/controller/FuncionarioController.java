package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.FuncionarioUpdateDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.application.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import java.net.URI;
import java.util.List;

@Tag(name = "Funcionários", description = "Gerenciamento de funcionários e veterinários")
@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "http://localhost:4200")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar novo funcionário", description = "Cadastra um funcionário (ou veterinário) e cria síncronamente seu usuário de acesso correspondente. Apenas administradores podem executar.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso.",
                    content = @Content(schema = @Schema(implementation = FuncionarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos (validações falharam).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas administradores têm permissão.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflito (CPF, CRMV ou E-mail já em uso no sistema).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<FuncionarioResponseDTO> criarFuncionario(@RequestBody FuncionarioRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        FuncionarioResponseDTO responseDTO = funcionarioService.criar(requestDTO);
        URI uri = uriBuilder.path("/api/funcionarios/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos os funcionários de forma paginada", description = "Retorna uma página de funcionários cadastrados.")
    public ResponseEntity<Page<FuncionarioResponseDTO>> buscarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(funcionarioService.listarTodos(pageable));
    }

    @GetMapping("/veterinarios")
    @Operation(summary = "Listar apenas os veterinários", description = "Retorna uma lista contendo todos os funcionários cujo cargo corresponde a VETERINÁRIO.")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarVeterinarios() {
        return ResponseEntity.ok(funcionarioService.listarVeterinarios());
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar funcionários pelo nome", description = "Retorna uma lista de funcionários cujo nome contém o termo pesquisado (case-insensitive).")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(funcionarioService.buscarPorNome(nome));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Retorna os detalhes de um funcionário com base no seu ID único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado."),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        FuncionarioResponseDTO responseDTO = funcionarioService.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar dados de um funcionário", description = "Atualiza os dados cadastrais, cargo ou CRMV de um funcionário. Apenas administradores podem executar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso.",
                    content = @Content(schema = @Schema(implementation = FuncionarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas administradores têm permissão.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflito (CPF, CRMV ou E-mail já utilizado por outro usuário).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<FuncionarioResponseDTO> atualizarFuncionario(@PathVariable Long id, @RequestBody FuncionarioUpdateDTO requestDTO) {
        FuncionarioResponseDTO responseDTO = funcionarioService.atualizar(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar funcionário por ID", description = "Remove um funcionário do sistema. Apenas administradores podem executar e a deleção é bloqueada se houver vínculos de serviços ativos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Falha ao deletar devido a restrições de chaves (ex: veterinário com serviços vinculados).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas administradores têm permissão.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
