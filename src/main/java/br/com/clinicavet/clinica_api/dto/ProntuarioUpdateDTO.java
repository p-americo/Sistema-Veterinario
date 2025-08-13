package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProntuarioUpdateDTO {
    
    @Size(max = 1000, message = "As alergias conhecidas devem ter no máximo 1000 caracteres.")
    private String alergiasConhecidas;
    
    @Size(max = 1000, message = "As condições preexistentes devem ter no máximo 1000 caracteres.")
    private String condicoesPreexistentes;
}