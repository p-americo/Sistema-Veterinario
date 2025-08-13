package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter

public class ClienteUpdateDTO {

    private String nome;

    private String cpf;

    private LocalDate dataNascimento;

    private String telefone;

    private String email;
}
