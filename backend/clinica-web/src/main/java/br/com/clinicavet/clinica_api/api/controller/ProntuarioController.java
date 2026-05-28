package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.application.service.ProntuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;

@Tag(name = "Prontuários", description = "Gerenciamento dos prontuários dos animais")
@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    @Autowired
    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @PostMapping
    public ResponseEntity<ProntuarioResponseDTO> criarProntuario(@RequestBody @Valid ProntuarioRequestDTO prontuarioRequestDTO,
                                                               UriComponentsBuilder uriComponentsBuilder) {
        ProntuarioResponseDTO responseDTO = prontuarioService.criar(prontuarioRequestDTO);
        URI uri = uriComponentsBuilder.path("/api/prontuarios/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ProntuarioResponseDTO>> buscarTodosProntuarios(@PageableDefault(size = 10) Pageable pageable) {
        Page<ProntuarioResponseDTO> listaProntuarios = prontuarioService.listarTodos(pageable);
        return ResponseEntity.ok(listaProntuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> buscarProntuarioPorId(@PathVariable Long id) {
        ProntuarioResponseDTO responseDTO = prontuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/{id}/completo")
    public ResponseEntity<ProntuarioResponseDTO> buscarProntuarioPorIdComRegistros(@PathVariable Long id) {
        ProntuarioResponseDTO responseDTO = prontuarioService.buscarPorIdComRegistros(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<ProntuarioResponseDTO> buscarProntuarioPorAnimalId(@PathVariable Long animalId) {
        ProntuarioResponseDTO responseDTO = prontuarioService.buscarPorAnimalId(animalId);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> atualizarProntuario(@PathVariable Long id,
                                                                   @RequestBody @Valid ProntuarioRequestDTO prontuarioRequestDTO) {
        ProntuarioResponseDTO responseDTO = prontuarioService.atualizar(id, prontuarioRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProntuario(@PathVariable Long id) {
        prontuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
