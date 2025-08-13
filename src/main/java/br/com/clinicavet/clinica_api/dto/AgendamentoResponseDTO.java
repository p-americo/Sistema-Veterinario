package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumAgendamento;
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
