package br.com.clinicavet.clinica_api.service.Interface;


import br.com.clinicavet.clinica_api.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.model.Internacao;



public interface InternacaoService extends BaseService<Internacao, Long, InternacaoRequestDTO, InternacaoResponseDTO> {


    InternacaoResponseDTO criar(InternacaoRequestDTO dto);

    InternacaoResponseDTO atualizar(Long id, InternacaoRequestDTO dto);

    InternacaoResponseDTO buscarInternacaoAtivaPorAnimalId(Long animalId);

    InternacaoResponseDTO darAltaInternacao(Long id);
}
