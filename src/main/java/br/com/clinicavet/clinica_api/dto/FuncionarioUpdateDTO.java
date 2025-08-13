package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioUpdateDTO {

    private String nome;

    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos.")
    private String cpf;

    @Past(message = "A data de nascimento deve ser no passado.")
    private LocalDate dataNascimento;

    private String telefone;

    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    @PastOrPresent(message = "A data de admissão não pode ser futura.")
    private LocalDate dataAdmissao;

    private String crmv;

    private Long cargoId; // Permite alterar o cargo
}