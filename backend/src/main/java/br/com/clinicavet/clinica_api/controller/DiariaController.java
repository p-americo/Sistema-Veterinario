package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.service.Interface.DiariaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/diarias-internacao")
public class DiariaController {

    private final DiariaService diariaService;

    public DiariaController(DiariaService diariaService) {
        this.diariaService = diariaService;
    }

    @PostMapping
    public ResponseEntity<DiariaResponseDTO> criar(@RequestBody @Valid DiariaRequestDTO dto, UriComponentsBuilder uriBuilder) {
        DiariaResponseDTO resposta = diariaService.criarDiaria(dto);
        URI uri = uriBuilder.path("/api/internacoes/diarias/{id}").buildAndExpand(resposta.getId()).toUri();
        return ResponseEntity.created(uri).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiariaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid DiariaRequestDTO dto) {
        DiariaResponseDTO resposta = diariaService.atualizarDiaria(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiariaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diariaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<DiariaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(diariaService.listarTodas());
    }

    @GetMapping("/internacao/{internacaoId}")
    public ResponseEntity<List<DiariaResponseDTO>> listarPorInternacao(@PathVariable Long internacaoId) {
        return ResponseEntity.ok(diariaService.listarPorInternacao(internacaoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        diariaService.deletarDiaria(id);
        return ResponseEntity.noContent().build();
    }
}
