package br.com.clinicavet.clinica_api.application.event;

import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.domain.model.Cliente;

public record ClienteCriadoEvent(ClienteRequestDTO clienteDTO, Cliente cliente) {}
