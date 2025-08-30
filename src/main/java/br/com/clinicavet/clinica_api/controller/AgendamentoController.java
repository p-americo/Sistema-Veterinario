package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.AgendamentoResponseDTO;
import br.com.clinicavet.clinica_api.service.Interface.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {


    private final AgendamentoService agendamentoService;

    @Autowired
    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criarAgendamento(@RequestBody @Valid AgendamentoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        AgendamentoResponseDTO responseDTO = agendamentoService.criarAgendamento(requestDTO);
        URI uri = uriBuilder.path("/api/agendamentos/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponseDTO>> listarAgendamentos() {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarAgendamentoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.listarPorId(id));
    }

    // --- Endpoints para AÇÕES (alteração de status) ---

    // PATCH é semanticamente bom para atualizações parciais ou de estado
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoResponseDTO> cancelarAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO responseDTO = agendamentoService.cancelarAgendamento(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/confirmar") // Mapeia para requisições PATCH
    public ResponseEntity<AgendamentoResponseDTO> confirmarAgendamento(@PathVariable Long id) {

        AgendamentoResponseDTO responseDTO = agendamentoService.confirmarAgendamento(id);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(@PathVariable Long id, @RequestBody AgendamentoRequestDTO updateDTO) {
        AgendamentoResponseDTO responseDTO = agendamentoService.atualizarAgendamento(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        agendamentoService.deletarAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}