package br.com.clinicavet.clinica_api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "DTO de requisição para cadastro de um novo Cliente")
public class ClienteRequestDTO {


    @Size(max = 100)
    @Schema(description = "Nome completo do cliente", example = "Maria Silva Santos")
    private String nome;

    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos.")
    @Schema(description = "CPF do cliente (apenas 11 dígitos numéricos)", example = "12345678901")
    private String cpf;

    @Past(message = "A data de nascimento deve ser uma data no passado.")   
    @Schema(description = "Data de nascimento do cliente", example = "1990-05-15")
    private LocalDate dataNascimento;

    @Size(max = 20)
    @Schema(description = "Telefone para contato do cliente", example = "11988887777")
    private String telefone;

    @Email(message = "O formato do e-mail é inválido.")
    @Size(max = 100)
    @Schema(description = "E-mail de contato e login do cliente", example = "maria.silva@email.com")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.")
    @Schema(description = "Senha de acesso do cliente (mínimo de 8 caracteres, com letra maiúscula, minúscula, número e caractere especial)", example = "Senha@123")
    private String senha;

}
