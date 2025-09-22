package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.service.Interface.ProntuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<List<ProntuarioResponseDTO>> buscarTodosProntuarios() {
        List<ProntuarioResponseDTO> listaProntuarios = prontuarioService.listarTodos();
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