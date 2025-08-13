package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.model.enums.EnumPorte;
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

public class AnimalUpdateDTO {

    @Size(max = 50)
    private String nome;

    private EnumEspecie especie;

    private EnumPorte porte;

    @Size(max = 50)
    private String raca;

    private EnumSexo sexo;

    @Size(max = 15)
    private String cor;

    @PositiveOrZero(message = "O peso do animal não pode ser negativo.")
    private BigDecimal peso;

    private Boolean castrado;

    @PastOrPresent
    private LocalDate dataNascimento;

    @Size(max = 100)
    private String observacao;

    @Positive(message = "O valor do ID deve ser positivo.")
    private Long clienteId;
}
