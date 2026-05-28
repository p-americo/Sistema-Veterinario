package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AgendamentoResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AgendamentoService {

    AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO requestDTO);

    void deletarAgendamento(Long id);

    List<AgendamentoResponseDTO> listarTodos();

    Page<AgendamentoResponseDTO> listarTodos(Pageable pageable);

    AgendamentoResponseDTO listarPorId(Long id);

    AgendamentoResponseDTO cancelarAgendamento(Long id);

    AgendamentoResponseDTO confirmarAgendamento(Long id);

    AgendamentoResponseDTO realizarAgendamento(Long id);

    AgendamentoResponseDTO atualizarAgendamento(Long id, AgendamentoRequestDTO updateDTO);


}
