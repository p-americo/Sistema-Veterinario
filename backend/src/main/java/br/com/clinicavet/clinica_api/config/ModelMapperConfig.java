package br.com.clinicavet.clinica_api.config;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();


      // Configurar modellMppaer depois
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}