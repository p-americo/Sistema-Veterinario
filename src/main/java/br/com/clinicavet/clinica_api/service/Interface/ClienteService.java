package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.dto.ClienteUpdateDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO criarCliente (ClienteRequestDTO cargo);

    ClienteResponseDTO atualizarCliente (Long id, ClienteUpdateDTO cargo);

    void deletarCliente(Long id);

    ClienteResponseDTO buscarPorId(Long id);
    
    List<ClienteResponseDTO> buscarPorNome(String nome);

    List<ClienteResponseDTO> listarTodos();
}
