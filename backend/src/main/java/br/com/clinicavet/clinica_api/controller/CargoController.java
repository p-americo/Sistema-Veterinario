package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.Execeptions.GlobalExceptionHandler;
import br.com.clinicavet.clinica_api.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.service.CargoServiceImplement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Cargos", description = "Gerenciamento de cargos dos funcionários")
@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoServiceImplement cargoServiceImplement;

    public CargoController(CargoServiceImplement cargoServiceImplement) {
        this.cargoServiceImplement = cargoServiceImplement;
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
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<CargoResponseDTO> criarCargo(@RequestBody @Valid CargoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        CargoResponseDTO responseDTO = cargoServiceImplement.criar(requestDTO);
        URI uri = uriBuilder.path("/api/cargos/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDTO>> listarCargos() {
        return ResponseEntity.ok(cargoServiceImplement.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> buscarCargoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cargoServiceImplement.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> atualizarCargo(@PathVariable Long id, @RequestBody @Valid CargoRequestDTO requestDTO) {
        CargoResponseDTO responseDTO = cargoServiceImplement.atualizar(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCargo(@PathVariable Long id) {
        cargoServiceImplement.deletar(id);
        return ResponseEntity.noContent().build();
    }
}