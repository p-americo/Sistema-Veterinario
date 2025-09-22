package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.model.Cliente;

import java.util.List;

public interface ClienteService extends BaseService<Cliente, Long, ClienteRequestDTO, ClienteResponseDTO>  {

    
    List<ClienteResponseDTO> buscarPorNome(String nome);


    ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateDTO dto);
}
