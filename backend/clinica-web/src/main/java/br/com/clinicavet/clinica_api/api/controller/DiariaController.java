package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.application.service.DiariaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;
import java.util.List;

@Tag(name = "Diárias de Internação", description = "Gerenciamento das diárias associadas às internações")
@RestController
@RequestMapping("/api/diarias-internacao")
public class DiariaController {

    private final DiariaService diariaService;

    public DiariaController(DiariaService diariaService) {
        this.diariaService = diariaService;
    }

    @PostMapping
    public ResponseEntity<DiariaResponseDTO> criar(@RequestBody @Valid DiariaRequestDTO dto, UriComponentsBuilder uriBuilder) {
        DiariaResponseDTO resposta = diariaService.criar(dto);
        URI uri = uriBuilder.path("/api/internacoes/diarias/{id}").buildAndExpand(resposta.getId()).toUri();
        return ResponseEntity.created(uri).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiariaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid DiariaRequestDTO dto) {
        DiariaResponseDTO resposta = diariaService.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiariaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diariaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<DiariaResponseDTO>> listarTodas(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(diariaService.listarTodos(pageable));
    }

    @GetMapping("/internacao/{internacaoId}")
    public ResponseEntity<List<DiariaResponseDTO>> listarPorInternacao(@PathVariable Long internacaoId) {
        return ResponseEntity.ok(diariaService.listarPorInternacao(internacaoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        diariaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
