package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.service.Interface.InternacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Internações", description = "Gerenciamento do ciclo de internações dos animais")
@RestController
@RequestMapping("/api/internacoes")
@CrossOrigin(origins = "http://localhost:4200") // Necessário para o Angular
public class InternacaoController {

    private final InternacaoService internacaoService;

    public InternacaoController(InternacaoService internacaoService) {
        this.internacaoService = internacaoService;
    }

    @PostMapping
    public ResponseEntity<InternacaoResponseDTO> criarInternacao(@RequestBody @Valid InternacaoRequestDTO dto, UriComponentsBuilder uriBuilder) {
        InternacaoResponseDTO resposta = internacaoService.criar(dto);
        URI uri = uriBuilder.path("/api/internacoes/{id}").buildAndExpand(resposta.getId()).toUri();
        return ResponseEntity.created(uri).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<InternacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(internacaoService.listarTodos());
    }

    @GetMapping("/animal/{animalId}/ativa")
    public ResponseEntity<InternacaoResponseDTO> buscarInternacaoAtivaPorAnimal(@PathVariable Long animalId) {
        return ResponseEntity.ok(internacaoService.buscarInternacaoAtivaPorAnimalId(animalId));
    }

    @PostMapping("/{id}/alta")
    public ResponseEntity<InternacaoResponseDTO> darAlta(@PathVariable Long id) {
        return ResponseEntity.ok(internacaoService.darAltaInternacao(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(internacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternacaoResponseDTO> atualizarInternacao(@PathVariable Long id, @RequestBody @Valid InternacaoRequestDTO dto) {
        return ResponseEntity.ok(internacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        internacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}