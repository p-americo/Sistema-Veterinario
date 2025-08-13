package br.com.clinicavet.clinica_api.service.Interface;


import br.com.clinicavet.clinica_api.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.dto.InternacaoResponseDTO;

import java.util.List;

public interface InternacaoServiceInterface {


    InternacaoResponseDTO criarInternacao(InternacaoRequestDTO dto);

    InternacaoResponseDTO atualizarInternacao(Long id, InternacaoRequestDTO dto);

    InternacaoResponseDTO buscarPorId(Long id);

    List<InternacaoResponseDTO> listarTodas();

    void deletarInternacao(Long id);
}
