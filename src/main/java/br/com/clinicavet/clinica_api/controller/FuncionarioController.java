package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.service.FuncionarioServiceImplement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "http://localhost:4200") // Adicionado para permitir acesso do frontend
public class FuncionarioController {

    private final FuncionarioServiceImplement funcionarioServiceImplement;

    public FuncionarioController(FuncionarioServiceImplement funcionarioServiceImplement) {
        this.funcionarioServiceImplement = funcionarioServiceImplement;
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> criarFuncionario(@RequestBody FuncionarioRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        FuncionarioResponseDTO responseDTO = funcionarioServiceImplement.criarFuncionario(requestDTO);
        URI uri = uriBuilder.path("/api/funcionarios/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(funcionarioServiceImplement.buscarTodos());
    }

    @GetMapping("/veterinarios")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarVeterinarios() {
        return ResponseEntity.ok(funcionarioServiceImplement.listarVeterinarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        FuncionarioResponseDTO responseDTO = funcionarioServiceImplement.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> atualizarFuncionario(@PathVariable Long id, @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO responseDTO = funcionarioServiceImplement.atualizarFuncionario(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioServiceImplement.deletarFuncionario(id);
        return ResponseEntity.noContent().build();
    }
}