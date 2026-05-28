package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Cargo;



public interface CargoService extends GenericCrudService<Cargo, Long, CargoRequestDTO, CargoResponseDTO> {

    void deletar(Long id);

    CargoResponseDTO criar(CargoRequestDTO requestDTO);

    CargoResponseDTO atualizar(Long id, CargoRequestDTO requestDTO);


}
