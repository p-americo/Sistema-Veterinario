package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.model.DiariaInternacao;
import java.util.List;


public interface DiariaService extends BaseService<DiariaInternacao, Long, DiariaRequestDTO, DiariaResponseDTO> {

    DiariaResponseDTO criar(DiariaRequestDTO dto);

    DiariaResponseDTO atualizar(Long id, DiariaRequestDTO dto);

    List<DiariaResponseDTO> listarPorInternacao(Long internacaoId);

}

