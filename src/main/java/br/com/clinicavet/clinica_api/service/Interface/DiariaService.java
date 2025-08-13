package br.com.clinicavet.clinica_api.service.Interface;
import br.com.clinicavet.clinica_api.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.dto.DiariaResponseDTO;

import java.util.List;


public interface DiariaService {

    DiariaResponseDTO criarDiaria(DiariaRequestDTO dto);

    DiariaResponseDTO atualizarDiaria(Long id, DiariaRequestDTO dto);

    DiariaResponseDTO buscarPorId(Long id);

    List<DiariaResponseDTO> listarTodas();

    List<DiariaResponseDTO> listarPorInternacao(Long internacaoId);

    void deletarDiaria(Long id);
}

