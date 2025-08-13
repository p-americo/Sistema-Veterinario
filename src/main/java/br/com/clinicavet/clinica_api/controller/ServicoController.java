package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.ServicoRequestDTO; // << DTO para entrada
import br.com.clinicavet.clinica_api.dto.ServicoResponseDTO; // << DTO para saída
import br.com.clinicavet.clinica_api.service.ServicoServiceImplement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoServiceImplement service;
    private final ServicoServiceImplement servicoServiceImplement;

    @Autowired
    public ServicoController(ServicoServiceImplement service, ServicoServiceImplement servicoServiceImplement) {
        this.service = service;
        this.servicoServiceImplement = servicoServiceImplement;
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listarServicos() {
        return ResponseEntity.ok(servicoServiceImplement.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> detalharServico(@PathVariable @NotNull Long id) {
        ServicoResponseDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> cadastrarServico(@RequestBody @Valid ServicoRequestDTO dto, UriComponentsBuilder uriBuilder) {

        ServicoResponseDTO servicoSalvo = service.cadastrarServico(dto);
        URI uri = uriBuilder.path("/api/servicos/{id}").buildAndExpand(servicoSalvo.getId()).toUri();
        return ResponseEntity.created(uri).body(servicoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizarServico(@PathVariable @NotNull Long id, @RequestBody @Valid ServicoRequestDTO dto) {

        ServicoResponseDTO servicoAtualizado = service.atualizarServico(id, dto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable @NotNull Long id) {

        service.deletarServico(id);
        return ResponseEntity.noContent().build();
    }
}