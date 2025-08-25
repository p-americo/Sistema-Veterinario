package br.com.clinicavet.clinica_api.dto;

import  lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenJWTDTO {
    private String token;
}