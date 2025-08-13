package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.model.enums.EnumSexo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AnimalRequestDTO {


    @NotBlank(message = "O nome do animal é obrigatório.")
    @Size(max = 50, message = "O nome do animal deve ter no máximo 50 caracteres.") // Ajuste o max se necessário
    private String nome;

    @NotNull(message = "A espécie do animal é obrigatória.")
    private EnumEspecie especie;

    @NotNull(message = "O porte do animal é obrigatório.")
    private EnumPorte porte;

    @NotBlank(message = "A raça do animal é obrigatória.")
    @Size(max = 50, message = "A raça deve ter no máximo 100 caracteres.")
    private String raca;

    @NotNull(message = "O sexo do animal é obrigatório.")
    private EnumSexo sexo;

    @Size(max = 15, message = "A cor deve ter no máximo 50 caracteres.")
    private String cor;

    @NotNull(message = "O peso do animal é obrigatório.")
    @PositiveOrZero(message = "O peso do animal não pode ser negativo.")
    private BigDecimal peso;

    @NotNull(message = "Informar se o animal é castrado é obrigatório.")
    private Boolean castrado;

    @NotNull(message = "A data de nascimento do animal é obrigatória.")
    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
    private LocalDate dataNascimento;

    @Size(max = 100, message = "A observação deve ter no máximo 100 caracteres.") // Ajuste conforme necessidade
    private String observacao;

    @NotNull(message = "O ID do cliente é obrigatório.")
    private Long clienteId;


}

