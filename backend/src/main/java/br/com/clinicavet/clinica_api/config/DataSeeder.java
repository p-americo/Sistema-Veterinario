package br.com.clinicavet.clinica_api.config;

import br.com.clinicavet.clinica_api.model.Cargo;
import br.com.clinicavet.clinica_api.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.repository.CargoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component // Marca esta classe como um bean do Spring para ser executado
public class DataSeeder implements CommandLineRunner {

    private final CargoRepository cargoRepository;

    public DataSeeder(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica se a tabela de cargos está vazia para não inserir dados duplicados a cada reinício
        if (cargoRepository.count() == 0) {
            System.out.println("Populando cargos iniciais...");
            Cargo vet = new Cargo();
            vet.setCargo(EnumCargo.VETERINARIO);
            vet.setSalario(new BigDecimal("8000.00"));

            Cargo rec = new Cargo();
            rec.setCargo(EnumCargo.RECEPCIONISTA);
            rec.setSalario(new BigDecimal("2500.00"));

            cargoRepository.saveAll(List.of(vet, rec));
            System.out.println("Cargos populados com sucesso.");
        }
    }
}