package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClienteRequestDTO {


    @Size(max = 100)
    private String nome;

    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos.")
    private String cpf;

    @Past(message = "A data de nascimento deve ser uma data no passado.")
    private LocalDate dataNascimento;

    @Size(max = 20)
    private String telefone;

    @Email(message = "O formato do e-mail é inválido.")
    @Size(max = 100)
    private String email;


}
