package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Prontuario;

public interface ProntuarioService extends GenericCrudService<Prontuario, Long, ProntuarioRequestDTO, ProntuarioResponseDTO> {

    ProntuarioResponseDTO criar(ProntuarioRequestDTO prontuarioRequestDTO);

    ProntuarioResponseDTO buscarPorAnimalId(Long animalId);

    ProntuarioResponseDTO buscarPorIdComRegistros(Long id);

    ProntuarioResponseDTO atualizar(Long id, ProntuarioRequestDTO prontuarioRequestDTO);
}
