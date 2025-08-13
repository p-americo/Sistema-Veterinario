package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoUpdateDTO;
import br.com.clinicavet.clinica_api.service.AdministracaoMedicamentoServiceImplement;
import br.com.clinicavet.clinica_api.service.Interface.AdminstracaoMedicamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/administracoes-medicamento")
public class AdministracaoMedicamentoController {

    private final AdminstracaoMedicamentoService administracaoMedicamentoService;

    @Autowired
    public AdministracaoMedicamentoController(AdministracaoMedicamentoServiceImplement administracaoMedicamentoService) {
        this.administracaoMedicamentoService = administracaoMedicamentoService;
    }

    @PostMapping
    public ResponseEntity<AdministracaoMedicamentoResponseDTO> criarAdministracao(@RequestBody @Valid AdministracaoMedicamentoRequestDTO administracaoRequestDTO, 
                                                                                 UriComponentsBuilder uriComponentsBuilder) {
        AdministracaoMedicamentoResponseDTO responseDTO = administracaoMedicamentoService.criarAdministracao(administracaoRequestDTO);

        URI uri = uriComponentsBuilder.path("/api/administracoes-medicamento/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AdministracaoMedicamentoResponseDTO>> buscarTodasAdministracoes() {
        List<AdministracaoMedicamentoResponseDTO> listaAdministracoes = administracaoMedicamentoService.listarTodos();
        return ResponseEntity.ok(listaAdministracoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministracaoMedicamentoResponseDTO> buscarAdministracaoPorId(@PathVariable Long id) {
        AdministracaoMedicamentoResponseDTO responseDTO = administracaoMedicamentoService.buscarPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/entrada-prontuario/{entradaProntuarioId}")
    public ResponseEntity<List<AdministracaoMedicamentoResponseDTO>> buscarAdministracoesPorEntradaProntuario(@PathVariable Long entradaProntuarioId) {
        List<AdministracaoMedicamentoResponseDTO> listaAdministracoes = administracaoMedicamentoService.buscarPorEntradaProntuarioId(entradaProntuarioId);
        return ResponseEntity.ok(listaAdministracoes);
    }

    @GetMapping("/medicamento/{medicamentoId}")
    public ResponseEntity<List<AdministracaoMedicamentoResponseDTO>> buscarAdministracoesPorMedicamento(@PathVariable Long medicamentoId) {
        List<AdministracaoMedicamentoResponseDTO> listaAdministracoes = administracaoMedicamentoService.buscarPorMedicamentoId(medicamentoId);
        return ResponseEntity.ok(listaAdministracoes);
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<AdministracaoMedicamentoResponseDTO>> buscarAdministracoesPorFuncionario(@PathVariable Long funcionarioId) {
        List<AdministracaoMedicamentoResponseDTO> listaAdministracoes = administracaoMedicamentoService.buscarPorFuncionarioId(funcionarioId);
        return ResponseEntity.ok(listaAdministracoes);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<AdministracaoMedicamentoResponseDTO>> buscarAdministracoesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<AdministracaoMedicamentoResponseDTO> listaAdministracoes = administracaoMedicamentoService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(listaAdministracoes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministracaoMedicamentoResponseDTO> atualizarAdministracao(@PathVariable Long id, 
                                                                                     @RequestBody @Valid AdministracaoMedicamentoUpdateDTO administracaoUpdateDTO) {
        AdministracaoMedicamentoResponseDTO responseDTO = administracaoMedicamentoService.atualizarAdministracao(id, administracaoUpdateDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAdministracao(@PathVariable Long id) {
        administracaoMedicamentoService.deletarAdministracao(id);
        return ResponseEntity.noContent().build();
    }
}