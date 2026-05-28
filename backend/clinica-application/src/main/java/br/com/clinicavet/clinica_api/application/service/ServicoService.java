package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ServicoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Servico;


public interface ServicoService extends GenericCrudService<Servico, Long, ServicoRequestDTO, ServicoResponseDTO> {

    ServicoResponseDTO cadastrar(ServicoRequestDTO servicoRequestDTO);

    ServicoResponseDTO atualizar(Long id, ServicoRequestDTO update);




}
