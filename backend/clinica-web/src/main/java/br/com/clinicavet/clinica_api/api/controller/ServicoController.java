package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ServicoResponseDTO;
import br.com.clinicavet.clinica_api.application.service.ServicoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;

@Tag(name = "Serviços", description = "Gerenciamento dos serviços oferecidos pela clínica")
@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    @Autowired
    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> listarServicos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(servicoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> detalharServico(@PathVariable @NotNull Long id) {
        ServicoResponseDTO dto = servicoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> cadastrarServico(@RequestBody @Valid ServicoRequestDTO dto, UriComponentsBuilder uriBuilder) {
        ServicoResponseDTO servicoSalvo = servicoService.cadastrar(dto);
        URI uri = uriBuilder.path("/api/servicos/{id}").buildAndExpand(servicoSalvo.getId()).toUri();
        return ResponseEntity.created(uri).body(servicoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizarServico(@PathVariable @NotNull Long id, @RequestBody @Valid ServicoRequestDTO dto) {
        ServicoResponseDTO servicoAtualizado = servicoService.atualizar(id, dto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable @NotNull Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
