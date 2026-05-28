package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.domain.model.Cliente;

import java.util.List;

public interface ClienteService extends GenericCrudService<Cliente, Long, ClienteRequestDTO, ClienteResponseDTO>  {

    
    List<ClienteResponseDTO> buscarPorNome(String nome);

    ClienteResponseDTO buscarPorCpf(String cpf);

    ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateDTO dto);
}
