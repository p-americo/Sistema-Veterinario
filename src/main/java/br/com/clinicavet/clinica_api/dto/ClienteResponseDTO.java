package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClienteResponseDTO {


    private Long id;

    private String nome;

    private String cpf;

    @Past
    private LocalDate dataNascimento;

    private String telefone;

    private String email;

    private LocalDate dataCadastro;
}
