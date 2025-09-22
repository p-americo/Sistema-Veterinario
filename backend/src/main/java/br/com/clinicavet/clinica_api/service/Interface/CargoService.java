package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.model.Cargo;



public interface CargoService extends BaseService<Cargo, Long, CargoRequestDTO, CargoResponseDTO> {

    void deletar(Long id);

    CargoResponseDTO criar(CargoRequestDTO requestDTO);

    CargoResponseDTO atualizar(Long id, CargoRequestDTO requestDTO);


}
