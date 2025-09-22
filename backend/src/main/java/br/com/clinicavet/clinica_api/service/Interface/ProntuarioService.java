package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.model.Prontuario;

public interface ProntuarioService extends BaseService<Prontuario, Long, ProntuarioRequestDTO, ProntuarioResponseDTO> {

    ProntuarioResponseDTO criar(ProntuarioRequestDTO prontuarioRequestDTO);

    ProntuarioResponseDTO buscarPorAnimalId(Long animalId);

    ProntuarioResponseDTO buscarPorIdComRegistros(Long id);

    ProntuarioResponseDTO atualizar(Long id, ProntuarioRequestDTO prontuarioRequestDTO);
}
