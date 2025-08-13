package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoRequestDTO {

        @NotNull
        private Long animalId;

        @NotNull
        private Long servicoId;

        @NotNull
        private Long clienteId;

        @NotNull @Future
        private LocalDateTime dataHoraAgendamento;

        @Size(max = 255)
        private String observacoes;

}
