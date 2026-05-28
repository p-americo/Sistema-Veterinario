package br.com.clinicavet.clinica_api.application.service;


import br.com.clinicavet.clinica_api.application.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Internacao;



public interface InternacaoService extends GenericCrudService<Internacao, Long, InternacaoRequestDTO, InternacaoResponseDTO> {


    InternacaoResponseDTO criar(InternacaoRequestDTO dto);

    InternacaoResponseDTO atualizar(Long id, InternacaoRequestDTO dto);

    InternacaoResponseDTO buscarInternacaoAtivaPorAnimalId(Long animalId);

    InternacaoResponseDTO darAltaInternacao(Long id);
}
