package br.com.clinicavet.clinica_api.application.dto;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumAgendamento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoResponseDTO {

    private Long id;
    private AnimalResponseDTO animal;

    private ServicoResponseDTO servico;

    private ClienteResponseDTO cliente;

    private LocalDateTime dataHoraAgendamento;

    private EnumAgendamento status;

    private String observacoes;
}
