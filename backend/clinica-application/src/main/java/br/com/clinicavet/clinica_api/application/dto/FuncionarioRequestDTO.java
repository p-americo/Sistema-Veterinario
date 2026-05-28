package br.com.clinicavet.clinica_api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "DTO de requisição para cadastro de um novo Funcionário ou Veterinário")
public class FuncionarioRequestDTO {

    // --- Campos herdados de EPessoa ---
    @NotBlank(message = "O nome é obrigatório.")
    @Schema(description = "Nome completo do funcionário", example = "Rodrigo Mendes Souza")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos.")
    @Schema(description = "CPF do funcionário (apenas 11 dígitos numéricos)", example = "98765432109")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser no passado.")
    @Schema(description = "Data de nascimento do funcionário", example = "1985-08-22")
    private LocalDate dataNascimento;

    @NotBlank(message = "O telefone é obrigatório.")
    @Schema(description = "Telefone de contato do funcionário", example = "11977776666")
    private String telefone;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    @Schema(description = "E-mail de contato e login do funcionário", example = "rodrigo.mendes@email.com")
    private String email;

    // --- Campos específicos de EFuncionario ---
    @NotNull(message = "A data de admissão é obrigatória.")
    @PastOrPresent(message = "A data de admissão não pode ser futura.")
    @Schema(description = "Data de admissão/contratação do funcionário", example = "2024-01-10")
    private LocalDate dataAdmissao;

    // O CRMV é opcional no DTO, a obrigatoriedade dele será validada no Service,
    // dependendo do cargo escolhido.
    @Pattern(
        regexp = "^(\\d{4,6}[-/]?[A-Z]{2})?$",
        message = "O CRMV deve seguir o padrão 12345-SP, 12345/SP ou 12345SP"
    )
    @Schema(description = "Registro do CRMV (obrigatório se o cargo for VETERINARIO)", example = "12345-SP")
    private String crmv;

    @NotNull(message = "O ID do cargo é obrigatório.")
    @Schema(description = "ID do cargo associado", example = "1")
    private Long cargoId; // << Recebemos o ID do cargo

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.")
    @Schema(description = "Senha de acesso do funcionário (mínimo de 8 caracteres, com letra maiúscula, minúscula, número e caractere especial)", example = "Senha@123")
    private String senha;
}