package br.com.clinicavet.clinica_api.application.event;

import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;

public record FuncionarioCriadoEvent(FuncionarioRequestDTO funcionarioDTO, Funcionario funcionario) {}
