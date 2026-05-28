package br.com.clinicavet.clinica_api.infrastructure.config;

import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CargoRepository cargoRepository,
                      FuncionarioRepository funcionarioRepository,
                      ClienteRepository clienteRepository,
                      UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder) {
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Popular cargos se não houver nenhum
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

        // 2. Popular usuário administrador de teste se não houver
        if (!usuarioRepository.existsByLogin("12345678900")) {
            System.out.println("Populando administrador de teste...");
            Cargo cargoVet = cargoRepository.findByCargo(EnumCargo.VETERINARIO)
                    .orElseThrow(() -> new RuntimeException("Cargo VETERINARIO não cadastrado"));

            Funcionario adminFunc = new Funcionario();
            adminFunc.setNome("Administrador de Teste");
            adminFunc.setCpf("12345678900");
            adminFunc.setDataNascimento(LocalDate.of(1985, 5, 20));
            adminFunc.setTelefone("11999999999");
            adminFunc.setEmail("admin@clinica.com");
            adminFunc.setDataAdmissao(LocalDate.now());
            adminFunc.alterarCargo(cargoVet, "123456");
            funcionarioRepository.save(adminFunc);

            Usuario adminUser = Usuario.criarUsuario(
                    "12345678900", // login = CPF
                    passwordEncoder.encode("Senha123!"),
                    EnumUsuarioRole.ROLE_ADMIN,
                    adminFunc
            );
            usuarioRepository.save(adminUser);
            System.out.println("Administrador de teste populado com sucesso.");
        }

        // 3. Popular usuário cliente de teste se não houver
        if (!usuarioRepository.existsByLogin("11111111111")) {
            System.out.println("Populando cliente de teste...");
            Cliente cliente = new Cliente();
            cliente.setNome("Cliente de Teste");
            cliente.setCpf("11111111111");
            cliente.setDataNascimento(LocalDate.of(1990, 8, 15));
            cliente.setTelefone("11888888888");
            cliente.setEmail("cliente@email.com");
            cliente.setDataCadastro(LocalDate.now());
            clienteRepository.save(cliente);

            Usuario clienteUser = Usuario.criarUsuario(
                    "11111111111", // login = CPF
                    passwordEncoder.encode("Senha123!"),
                    EnumUsuarioRole.ROLE_CLIENTE,
                    cliente
            );
            usuarioRepository.save(clienteUser);
            System.out.println("Cliente de teste populado com sucesso.");
        }
    }
}
