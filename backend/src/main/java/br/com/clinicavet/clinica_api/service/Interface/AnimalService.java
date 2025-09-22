package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.model.Animal;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


public interface AnimalService extends  BaseService<Animal, Long, AnimalRequestDTO, AnimalResponseDTO> {

    AnimalResponseDTO criar(AnimalRequestDTO animalDTO, MultipartFile arquivoImagem) throws IOException;

    AnimalResponseDTO atualizar(Long animalId, AnimalRequestDTO animalDTO);

    AnimalResponseDTO buscarPorId(Long id);

    byte[] buscarImagemPorIdAnimal(Long id);

}
