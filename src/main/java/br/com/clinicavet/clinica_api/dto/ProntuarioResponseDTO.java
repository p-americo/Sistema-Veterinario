package br.com.clinicavet.clinica_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProntuarioResponseDTO {
    
    private Long id;

    private Long animalId;

    private String nomeAnimal;

    private String alergiasConhecidas;

    private String condicoesPreexistentes;

    private List<RegistroProntuarioResponseDTO> registros;
}