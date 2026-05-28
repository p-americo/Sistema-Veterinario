package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DiariaService {

    DiariaResponseDTO criar(DiariaRequestDTO dto);

    DiariaResponseDTO atualizar(Long id, DiariaRequestDTO dto);

    DiariaResponseDTO buscarPorId(Long id);

    Page<DiariaResponseDTO> listarTodos(Pageable pageable);

    List<DiariaResponseDTO> listarPorInternacao(Long internacaoId);

    void deletar(Long id);
}

