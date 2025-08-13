package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.AgendamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.AgendamentoUpdateDTO;
import br.com.clinicavet.clinica_api.service.AgendamentoServiceImplement;
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


    private final AgendamentoServiceImplement agendamentoServiceImplement;

    @Autowired
    public AgendamentoController(AgendamentoServiceImplement agendamentoServiceImplement) {
        this.agendamentoServiceImplement = agendamentoServiceImplement;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criarAgendamento(@RequestBody @Valid AgendamentoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        AgendamentoResponseDTO responseDTO = agendamentoServiceImplement.criarAgendamento(requestDTO);
        URI uri = uriBuilder.path("/api/agendamentos/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponseDTO>> listarAgendamentos() {
        return ResponseEntity.ok(agendamentoServiceImplement.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarAgendamentoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoServiceImplement.listarPorId(id));
    }

    // --- Endpoints para AÇÕES (alteração de status) ---

    // PATCH é semanticamente bom para atualizações parciais ou de estado
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoResponseDTO> cancelarAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO responseDTO = agendamentoServiceImplement.cancelarAgendamento(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/confirmar") // Mapeia para requisições PATCH
    public ResponseEntity<AgendamentoResponseDTO> confirmarAgendamento(@PathVariable Long id) {

        AgendamentoResponseDTO responseDTO = agendamentoServiceImplement.confirmarAgendamento(id);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(@PathVariable Long id, @RequestBody @Valid AgendamentoUpdateDTO updateDTO) {
        AgendamentoResponseDTO responseDTO = agendamentoServiceImplement.atualizarAgendamento(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        agendamentoServiceImplement.deletarAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}