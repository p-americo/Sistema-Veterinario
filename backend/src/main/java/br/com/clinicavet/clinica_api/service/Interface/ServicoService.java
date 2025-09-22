package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.dto.ServicoResponseDTO;
import br.com.clinicavet.clinica_api.model.Servico;


public interface ServicoService extends BaseService<Servico, Long, ServicoRequestDTO, ServicoResponseDTO> {

    ServicoResponseDTO cadastrar(ServicoRequestDTO servicoRequestDTO);

    ServicoResponseDTO atualizar(Long id, ServicoRequestDTO update);




}
