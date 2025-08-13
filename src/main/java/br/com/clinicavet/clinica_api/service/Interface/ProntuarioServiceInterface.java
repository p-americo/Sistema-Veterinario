package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioUpdateDTO;

import java.util.List;

public interface ProntuarioServiceInterface {

    ProntuarioResponseDTO criarProntuario(ProntuarioRequestDTO prontuarioRequestDTO);

    ProntuarioResponseDTO atualizarProntuario(Long id, ProntuarioUpdateDTO prontuarioUpdateDTO);

    void deletarProntuario(Long id);

    ProntuarioResponseDTO buscarPorId(Long id);

    ProntuarioResponseDTO buscarPorAnimalId(Long animalId);

    List<ProntuarioResponseDTO> listarTodos();

    ProntuarioResponseDTO buscarPorIdComRegistros(Long id);
}

