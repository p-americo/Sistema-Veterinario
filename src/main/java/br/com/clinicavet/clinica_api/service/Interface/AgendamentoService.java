package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.AgendamentoResponseDTO;

import java.util.List;

public interface AgendamentoService {

    AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO requestDTO);

    void deletarAgendamento(Long id);

    List<AgendamentoResponseDTO> listarTodos();

    AgendamentoResponseDTO listarPorId(Long id);

    AgendamentoResponseDTO cancelarAgendamento(Long id);

    AgendamentoResponseDTO confirmarAgendamento(Long id);

    AgendamentoResponseDTO realizarAgendamento(Long id);



}
