package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoUpdateDTO;
import br.com.clinicavet.clinica_api.service.Interface.MedicamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @Autowired
    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> criarMedicamento(@RequestBody @Valid MedicamentoRequestDTO medicamentoRequestDTO,
                                                                   UriComponentsBuilder uriComponentsBuilder) {
        MedicamentoResponseDTO responseDTO = medicamentoService.criarMedicamento(medicamentoRequestDTO);

        URI uri = uriComponentsBuilder.path("/api/medicamentos/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listarTodosMedicamentos() {
        List<MedicamentoResponseDTO> listaMedicamentos = medicamentoService.listarTodos();
        return ResponseEntity.ok(listaMedicamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> buscarMedicamentoPorId(@PathVariable Long id) {
        MedicamentoResponseDTO responseDTO = medicamentoService.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> atualizarMedicamento(@PathVariable Long id,
                                                                       @RequestBody @Valid MedicamentoUpdateDTO medicamentoUpdateDTO) {
        MedicamentoResponseDTO responseDTO = medicamentoService.atualizarMedicamento(id, medicamentoUpdateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedicamento(@PathVariable Long id) {
        medicamentoService.deletarMedicamento(id);
        return ResponseEntity.noContent().build();
    }
}
