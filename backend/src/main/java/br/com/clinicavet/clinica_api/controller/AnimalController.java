package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.service.Interface.AnimalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Garante que o endpoint só aceita multipart
    public ResponseEntity<AnimalResponseDTO> criarAnimal(@RequestPart("dados") String dadosAnimalJson,
                                                         @RequestPart("imagem") MultipartFile arquivoImagem,
                                                         UriComponentsBuilder uriComponentsBuilder) throws IOException {

        // 1. Conversão manual do JSON (em formato String) para o DTO
        AnimalRequestDTO animalRequestDTO = objectMapper.readValue(dadosAnimalJson, AnimalRequestDTO.class);

        // 2. Chamada do serviço com os objetos já convertidos e separados
        AnimalResponseDTO responseDTO = animalService.criarAnimal(animalRequestDTO, arquivoImagem);

        URI uri = uriComponentsBuilder.path("/api/animais/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listarTodos() {
        List<AnimalResponseDTO> listaAnimais = animalService.listarTodos();
        return  ResponseEntity.ok(listaAnimais);
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> getImagemDoAnimal(@PathVariable Long id) {
        byte[] imagemBytes = animalService.buscarImagemPorIdAnimal(id);

        if (imagemBytes == null) {
            return ResponseEntity.notFound().build();
        }

        // Retorna os bytes da imagem com o Content-Type correto para o navegador exibir
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Ou IMAGE_PNG, dependendo do tipo
                .body(imagemBytes);
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
