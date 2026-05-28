package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AgendamentoResponseDTO;
import br.com.clinicavet.clinica_api.application.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "Agendamentos", description = "Gerenciamento de consultas e agendamentos de serviços na clínica")
@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @Autowired
    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    @Operation(summary = "Criar novo agendamento", description = "Agenda um serviço/consulta para um animal de um cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso.",
                    content = @Content(schema = @Schema(implementation = AgendamentoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos (validações falharam ou regras de negócio violadas).",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AgendamentoResponseDTO> criarAgendamento(@RequestBody @Valid AgendamentoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        AgendamentoResponseDTO responseDTO = agendamentoService.criarAgendamento(requestDTO);
        URI uri = uriBuilder.path("/api/agendamentos/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos de forma paginada", description = "Retorna uma página contendo todos os agendamentos registrados.")
    public ResponseEntity<Page<AgendamentoResponseDTO>> listarAgendamentos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(agendamentoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID", description = "Retorna os detalhes de um agendamento específico com base no seu ID único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado com o ID especificado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AgendamentoResponseDTO> buscarAgendamentoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.listarPorId(id));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar um agendamento", description = "Muda o status do agendamento informado para CANCELADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AgendamentoResponseDTO> cancelarAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO responseDTO = agendamentoService.cancelarAgendamento(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar um agendamento", description = "Muda o status do agendamento informado para CONFIRMADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento confirmado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AgendamentoResponseDTO> confirmarAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO responseDTO = agendamentoService.confirmarAgendamento(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um agendamento", description = "Permite alterar a data/hora, observações e associações de animal, serviço ou cliente de um agendamento existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso.",
                    content = @Content(schema = @Schema(implementation = AgendamentoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "404", description = "Agendamento ou entidades relacionadas não encontradas.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(@PathVariable Long id, @RequestBody AgendamentoRequestDTO updateDTO) {
        AgendamentoResponseDTO responseDTO = agendamentoService.atualizarAgendamento(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um agendamento", description = "Exclui permanentemente um agendamento do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        agendamentoService.deletarAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}
