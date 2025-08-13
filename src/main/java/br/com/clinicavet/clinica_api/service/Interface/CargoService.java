package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.dto.CargoResponseDTO;

import java.util.List;

public interface CargoService {

    CargoResponseDTO criarCargo (CargoRequestDTO cargo);

    CargoResponseDTO buscarPorId(Long id);

    List<CargoResponseDTO> listarTodos();

    CargoResponseDTO atualizarCargo (Long id, CargoRequestDTO cargo);

    void deletarCargo(Long id);


}
