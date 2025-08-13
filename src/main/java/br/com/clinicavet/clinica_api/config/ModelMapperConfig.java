package br.com.clinicavet.clinica_api.config;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Você pode adicionar configurações customizadas ao modelMapper aqui se necessário
        //evita alguns mapeamentos inesperados
        // Ex: modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}
