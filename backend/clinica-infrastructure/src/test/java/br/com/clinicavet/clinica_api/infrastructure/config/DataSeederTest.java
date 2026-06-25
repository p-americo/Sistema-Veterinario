package br.com.clinicavet.clinica_api.infrastructure.config;

import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSeederTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DataSeeder dataSeeder;

    @BeforeEach
    void setUp() {
        dataSeeder = new DataSeeder(cargoRepository, funcionarioRepository, clienteRepository, usuarioRepository, passwordEncoder);

        when(usuarioRepository.existsByLogin("12345678900")).thenReturn(true);
        when(usuarioRepository.existsByLogin("11111111111")).thenReturn(true);
    }

    @Test
    void run_ComCargosJaPopulados_NaoPopulaCargos() throws Exception {
        when(cargoRepository.count()).thenReturn(2L);

        dataSeeder.run();

        verify(cargoRepository, never()).saveAll(any());
    }

    @Test
    void run_SemCargos_PopulaCargosIniciais() throws Exception {
        when(cargoRepository.count()).thenReturn(0L);

        dataSeeder.run();

        verify(cargoRepository, times(1)).saveAll(argThat((List<Cargo> cargos) ->
                cargos.size() == 2
                        && cargos.stream().anyMatch(c -> c.getCargo() == EnumCargo.VETERINARIO)
                        && cargos.stream().anyMatch(c -> c.getCargo() == EnumCargo.RECEPCIONISTA)));
    }

    @Test
    void run_ComAdminJaExistente_NaoPopulaAdmin() throws Exception {
        when(cargoRepository.count()).thenReturn(2L);

        dataSeeder.run();

        verify(funcionarioRepository, never()).save(any());
        verify(cargoRepository, never()).findByCargo(any());
    }

    @Test
    void run_SemAdmin_PopulaAdministradorDeTeste() throws Exception {
        when(cargoRepository.count()).thenReturn(2L);
        when(usuarioRepository.existsByLogin("12345678900")).thenReturn(false);

        Cargo cargoVeterinario = new Cargo();
        cargoVeterinario.setId(1L);
        cargoVeterinario.setCargo(EnumCargo.VETERINARIO);
        when(cargoRepository.findByCargo(EnumCargo.VETERINARIO)).thenReturn(Optional.of(cargoVeterinario));
        when(passwordEncoder.encode("Senha123!")).thenReturn("hashAdmin");

        dataSeeder.run();

        verify(funcionarioRepository, times(1)).save(argThat(f -> "12345678900".equals(f.getCpf())));
        verify(usuarioRepository, times(1)).save(argThat((Usuario u) -> "12345678900".equals(u.getLogin())));
    }

    @Test
    void run_SemAdminECargoVeterinarioNaoCadastrado_LancaExcecao() {
        when(cargoRepository.count()).thenReturn(2L);
        when(usuarioRepository.existsByLogin("12345678900")).thenReturn(false);
        when(cargoRepository.findByCargo(EnumCargo.VETERINARIO)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, dataSeeder::run);

        assertEquals("Cargo VETERINARIO não cadastrado", exception.getMessage());
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    void run_ComClienteJaExistente_NaoPopulaCliente() throws Exception {
        when(cargoRepository.count()).thenReturn(2L);

        dataSeeder.run();

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void run_SemCliente_PopulaClienteDeTeste() throws Exception {
        when(cargoRepository.count()).thenReturn(2L);
        when(usuarioRepository.existsByLogin("11111111111")).thenReturn(false);
        when(passwordEncoder.encode("Senha123!")).thenReturn("hashCliente");

        dataSeeder.run();

        verify(clienteRepository, times(1)).save(argThat((Cliente c) -> "11111111111".equals(c.getCpf())));
        verify(usuarioRepository, times(1)).save(argThat((Usuario u) -> "11111111111".equals(u.getLogin())));
    }
}
