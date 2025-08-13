package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoUpdateDTO {


    @Future(message = "A nova data do agendamento deve ser futura.")
    private LocalDateTime dataHoraAgendamento;

    @Size(max = 255)
    private String observacoes;

}