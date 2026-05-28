package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.application.service.RegistroProntuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Registros de Prontuário", description = "Gerenciamento dos registros (evoluções) dentro de um prontuário")
@RestController
@RequestMapping("/api/registros-prontuario")
public class RegistroProntuarioController {

    private final RegistroProntuarioService registroProntuarioService;

    @Autowired
    public RegistroProntuarioController(RegistroProntuarioService registroProntuarioService) {
        this.registroProntuarioService = registroProntuarioService;
    }

    @PostMapping
    public ResponseEntity<RegistroProntuarioResponseDTO> criarRegistro(@RequestBody @Valid RegistroProntuarioRequestDTO registroRequestDTO,
                                                                      UriComponentsBuilder uriComponentsBuilder) {
        RegistroProntuarioResponseDTO responseDTO = registroProntuarioService.criar(registroRequestDTO);
        URI uri = uriComponentsBuilder.path("/api/registros-prontuario/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<RegistroProntuarioResponseDTO>> buscarTodosRegistros(@PageableDefault(size = 10) Pageable pageable) {
        Page<RegistroProntuarioResponseDTO> listaRegistros = registroProntuarioService.listarTodos(pageable);
        return ResponseEntity.ok(listaRegistros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroProntuarioResponseDTO> buscarRegistroPorId(@PathVariable Long id) {
        RegistroProntuarioResponseDTO responseDTO = registroProntuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/{id}/medicamentos")
    public ResponseEntity<RegistroProntuarioResponseDTO> buscarRegistroPorIdComMedicamentos(@PathVariable Long id) {
        RegistroProntuarioResponseDTO responseDTO = registroProntuarioService.buscarPorIdComMedicamentos(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/prontuario/{prontuarioId}")
    public ResponseEntity<List<RegistroProntuarioResponseDTO>> buscarRegistrosPorProntuarioId(@PathVariable Long prontuarioId) {
        List<RegistroProntuarioResponseDTO> listaRegistros = registroProntuarioService.buscarPorProntuarioId(prontuarioId);
        return ResponseEntity.ok(listaRegistros);
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<RegistroProntuarioResponseDTO>> buscarRegistrosPorVeterinarioId(@PathVariable Long veterinarioId) {
        List<RegistroProntuarioResponseDTO> listaRegistros = registroProntuarioService.buscarPorVeterinarioId(veterinarioId);
        return ResponseEntity.ok(listaRegistros);
    }

    @GetMapping("/internacao/{internacaoId}")
    public ResponseEntity<List<RegistroProntuarioResponseDTO>> buscarRegistrosPorInternacaoId(@PathVariable Long internacaoId) {
        List<RegistroProntuarioResponseDTO> listaRegistros = registroProntuarioService.buscarPorInternacaoId(internacaoId);
        return ResponseEntity.ok(listaRegistros);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RegistroProntuarioResponseDTO>> buscarRegistrosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<RegistroProntuarioResponseDTO> listaRegistros = registroProntuarioService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(listaRegistros);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroProntuarioResponseDTO> atualizarRegistro(@PathVariable Long id,
                                                                          @RequestBody @Valid RegistroProntuarioRequestDTO registroRequestDTO) {
        RegistroProntuarioResponseDTO responseDTO = registroProntuarioService.atualizar(id, registroRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRegistro(@PathVariable Long id) {
        registroProntuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
