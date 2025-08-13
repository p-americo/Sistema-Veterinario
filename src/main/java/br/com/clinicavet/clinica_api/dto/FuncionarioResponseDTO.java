package br.com.clinicavet.clinica_api.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioResponseDTO {
    private Long id;

    private String nome;

    private String cpf;

    private LocalDate dataNascimento;

    private String telefone;

    private String email;

    private LocalDate dataAdmissao;

    private String crmv;

    private CargoResponseDTO cargo;
}