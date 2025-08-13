package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.dto.AnimalUpdateDTO;
import br.com.clinicavet.clinica_api.service.AnimalServiceImplement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    private AnimalServiceImplement animalServiceImplement;

    @Autowired
    public AnimalController(AnimalServiceImplement animalServiceImplement) {
        this.animalServiceImplement = animalServiceImplement;
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> criarAnimal(@RequestBody @Valid AnimalRequestDTO animalRequestDTO, UriComponentsBuilder uriComponentsBuilder) {
        AnimalResponseDTO responseDTO = animalServiceImplement.criarAnimal(animalRequestDTO);

        URI uri = uriComponentsBuilder.path("/api/animais/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listarTodos() {
        List<AnimalResponseDTO> listaAnimais = animalServiceImplement.listarTodos();
        return  ResponseEntity.ok(listaAnimais);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscarAnimalPorId(@PathVariable long id){
        AnimalResponseDTO responseDTO = animalServiceImplement.buscarAnimalPorId(id);
        return ResponseEntity.ok().body(responseDTO);

    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizarAnimal(@PathVariable long id, @RequestBody @Valid AnimalUpdateDTO animalDTO) {

        AnimalResponseDTO responseDTO = animalServiceImplement.atualizarAnimal(id, animalDTO);
        return ResponseEntity.ok().body(responseDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAnimal(@PathVariable long id) {
        animalServiceImplement.deletarAnimal(id);
        return ResponseEntity.noContent().build();
    }
}
