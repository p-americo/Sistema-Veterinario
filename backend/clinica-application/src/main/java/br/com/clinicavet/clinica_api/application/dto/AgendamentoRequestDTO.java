package br.com.clinicavet.clinica_api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "DTO de requisição para agendamento de uma consulta ou serviço")
public class AgendamentoRequestDTO {

        @NotNull
        @Schema(description = "ID do animal que receberá o atendimento", example = "3")
        private Long animalId;

        @NotNull
        @Schema(description = "ID do serviço a ser realizado (consulta, vacinação, etc.)", example = "1")
        private Long servicoId;

        @NotNull
        @Schema(description = "ID do cliente (tutor) dono do animal", example = "2")
        private Long clienteId;

        @NotNull @Future(message = "A nova data do agendamento deve ser futura.")
        @Schema(description = "Data e hora agendadas para o atendimento", example = "2026-06-01T14:30:00")
        private LocalDateTime dataHoraAgendamento;

        @Size(max = 255)
        @Schema(description = "Observações ou sintomas descritos pelo cliente", example = "Animal está apático e sem comer desde ontem.")
        private String observacoes;

}
