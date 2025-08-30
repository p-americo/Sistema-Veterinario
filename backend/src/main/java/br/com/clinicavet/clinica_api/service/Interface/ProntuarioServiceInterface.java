package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioResponseDTO;

import java.util.List;

public interface ProntuarioServiceInterface {

    ProntuarioResponseDTO criarProntuario(ProntuarioRequestDTO prontuarioRequestDTO);

    void deletarProntuario(Long id);

    ProntuarioResponseDTO buscarPorId(Long id);

    ProntuarioResponseDTO buscarPorAnimalId(Long animalId);

    List<ProntuarioResponseDTO> listarTodos();

    ProntuarioResponseDTO buscarPorIdComRegistros(Long id);

    ProntuarioResponseDTO atualizarProntuario(Long id, ProntuarioRequestDTO prontuarioRequestDTO);
}
