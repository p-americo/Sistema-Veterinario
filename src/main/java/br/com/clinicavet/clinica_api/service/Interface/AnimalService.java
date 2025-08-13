package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.dto.AnimalUpdateDTO;

import java.util.List;

public interface AnimalService {

    AnimalResponseDTO criarAnimal(AnimalRequestDTO animalRequestDTO);

    AnimalResponseDTO atualizarAnimal(Long id, AnimalUpdateDTO animalUpdateDTO);

    void deletarAnimal(Long id);

    AnimalResponseDTO buscarPorId(Long id);

    List<AnimalResponseDTO> listarTodos();



}
