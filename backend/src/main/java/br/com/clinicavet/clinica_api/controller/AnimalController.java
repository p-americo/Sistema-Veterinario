package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.service.Interface.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    private final AnimalService animalService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> criarAnimal(@RequestBody AnimalRequestDTO animalRequestDTO, UriComponentsBuilder uriComponentsBuilder) {
        AnimalResponseDTO responseDTO = animalService.criarAnimal(animalRequestDTO);

        URI uri = uriComponentsBuilder.path("/api/animais/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listarTodos() {
        List<AnimalResponseDTO> listaAnimais = animalService.listarTodos();
        return  ResponseEntity.ok(listaAnimais);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscarAnimalPorId(@PathVariable long id){
        AnimalResponseDTO responseDTO = animalService.buscarPorId(id);
        return ResponseEntity.ok().body(responseDTO);

    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizarAnimal(@PathVariable long id, @RequestBody AnimalRequestDTO animalDTO) {

        AnimalResponseDTO responseDTO = animalService.atualizarAnimal(id, animalDTO);
        return ResponseEntity.ok().body(responseDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAnimal(@PathVariable long id) {
        animalService.deletarAnimal(id);
        return ResponseEntity.noContent().build();
    }
}
