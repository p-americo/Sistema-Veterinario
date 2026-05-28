package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.api.exception.GlobalExceptionHandler;
import br.com.clinicavet.clinica_api.application.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.application.service.CargoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import java.net.URI;

@Tag(name = "Cargos", description = "Gerenciamento de cargos dos funcionários")
@RestController
@RequestMapping("/api/cargos")
@PreAuthorize("hasRole('ADMIN')")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @Operation(
            summary = "Cria um novo cargo",
            description = "Cria um novo cargo. Não é permitido criar cargos duplicados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cargo criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CargoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe um cargo com esse nome",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @PostMapping
    public ResponseEntity<CargoResponseDTO> criarCargo(@RequestBody @Valid CargoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        CargoResponseDTO responseDTO = cargoService.criar(requestDTO);
        URI uri = uriBuilder.path("/api/cargos/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<CargoResponseDTO>> listarCargos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(cargoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> buscarCargoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cargoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> atualizarCargo(@PathVariable Long id, @RequestBody @Valid CargoRequestDTO requestDTO) {
        CargoResponseDTO responseDTO = cargoService.atualizar(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCargo(@PathVariable Long id) {
        cargoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
