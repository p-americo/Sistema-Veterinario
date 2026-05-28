package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.application.service.InternacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;

@Tag(name = "Internações", description = "Gerenciamento do ciclo de internações dos animais")
@RestController
@RequestMapping("/api/internacoes")
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<Page<InternacaoResponseDTO>> listarTodas(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(internacaoService.listarTodos(pageable));
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
