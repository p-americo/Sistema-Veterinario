package br.com.clinicavet.clinica_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SenhaUpdateDTO {

        private String senhaAtual;
        private String novaSenha;
        private String confirmarNovaSenha;
}
