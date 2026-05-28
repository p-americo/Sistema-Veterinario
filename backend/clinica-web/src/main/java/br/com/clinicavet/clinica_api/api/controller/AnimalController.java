package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.application.service.AnimalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.io.IOException;
import java.net.URI;

@Tag(name = "Animais", description = "Gerenciamento dos animais cadastrados")
@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    private final AnimalService animalService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AnimalController(AnimalService animalService, ObjectMapper objectMapper) {
        this.animalService = animalService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnimalResponseDTO> criarAnimal(@RequestPart("dados") String dadosAnimalJson,
                                                         @RequestPart("imagem") MultipartFile arquivoImagem,
                                                         UriComponentsBuilder uriComponentsBuilder) throws IOException {
        AnimalRequestDTO animalRequestDTO = objectMapper.readValue(dadosAnimalJson, AnimalRequestDTO.class);
        AnimalResponseDTO responseDTO = animalService.criar(animalRequestDTO, arquivoImagem);
        URI uri = uriComponentsBuilder.path("/api/animais/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<AnimalResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        Page<AnimalResponseDTO> listaAnimais = animalService.listarTodos(pageable);
        return ResponseEntity.ok(listaAnimais);
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> getImagemDoAnimal(@PathVariable Long id) {
        byte[] imagemBytes = animalService.buscarImagemPorIdAnimal(id);
        if (imagemBytes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagemBytes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscarAnimalPorId(@PathVariable long id) {
        AnimalResponseDTO responseDTO = animalService.buscarPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizarAnimal(@PathVariable long id, @RequestBody AnimalRequestDTO animalDTO) {
        AnimalResponseDTO responseDTO = animalService.atualizar(id, animalDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAnimal(@PathVariable long id) {
        animalService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
