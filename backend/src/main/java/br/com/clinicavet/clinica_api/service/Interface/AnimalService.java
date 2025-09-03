package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AnimalService {

    AnimalResponseDTO criarAnimal(AnimalRequestDTO animalDTO, MultipartFile arquivoImagem) throws IOException;

    AnimalResponseDTO buscarAnimalPorId(long animalId);

    List<AnimalResponseDTO> listarTodos();

    AnimalResponseDTO atualizarAnimal(Long animalId, AnimalRequestDTO animalDTO);

    void deletarAnimal(Long id);

    AnimalResponseDTO buscarPorId(Long id);


    byte[] buscarImagemPorIdAnimal(Long id);

}
