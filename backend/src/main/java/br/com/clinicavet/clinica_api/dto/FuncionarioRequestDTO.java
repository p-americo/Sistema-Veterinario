package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioRequestDTO {

    // --- Campos herdados de EPessoa ---
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos.")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser no passado.")
    private LocalDate dataNascimento;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefone;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    // --- Campos específicos de EFuncionario ---
    @NotNull(message = "A data de admissão é obrigatória.")
    @PastOrPresent(message = "A data de admissão não pode ser futura.")
    private LocalDate dataAdmissao;

    // O CRMV é opcional no DTO, a obrigatoriedade dele será validada no Service,
    // dependendo do cargo escolhido.
    @Pattern(
        regexp = "^(\\d{4,6}[-/]?[A-Z]{2})?$",
        message = "O CRMV deve seguir o padrão 12345-SP, 12345/SP ou 12345SP"
    )
    private String crmv;

    @NotNull(message = "O ID do cargo é obrigatório.")
    private Long cargoId; // << Recebemos o ID do cargo

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.")
    private String senha;
}