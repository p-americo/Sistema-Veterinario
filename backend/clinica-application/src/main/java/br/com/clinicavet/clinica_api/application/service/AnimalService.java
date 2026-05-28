package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


public interface AnimalService extends GenericCrudService<Animal, Long, AnimalRequestDTO, AnimalResponseDTO> {

    AnimalResponseDTO criar(AnimalRequestDTO animalDTO, MultipartFile arquivoImagem) throws IOException;

    AnimalResponseDTO atualizar(Long animalId, AnimalRequestDTO animalDTO);

    AnimalResponseDTO buscarPorId(Long id);

    byte[] buscarImagemPorIdAnimal(Long id);

}
