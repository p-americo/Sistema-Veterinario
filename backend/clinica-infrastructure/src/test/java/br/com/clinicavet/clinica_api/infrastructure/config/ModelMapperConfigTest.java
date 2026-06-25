package br.com.clinicavet.clinica_api.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

class ModelMapperConfigTest {

    @Test
    void modelMapper_RetornaInstanciaComSkipNullHabilitado() {
        ModelMapper modelMapper = new ModelMapperConfig().modelMapper();

        assertNotNull(modelMapper);
        assertTrue(modelMapper.getConfiguration().isSkipNullEnabled());
    }
}
