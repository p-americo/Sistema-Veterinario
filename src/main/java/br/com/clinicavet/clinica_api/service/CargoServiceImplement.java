package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.model.Cargo;
import br.com.clinicavet.clinica_api.repository.CargoRepository;
import br.com.clinicavet.clinica_api.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.service.Interface.CargoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CargoServiceImplement implements CargoService {

    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ModelMapper modelMapper;

    public CargoServiceImplement(CargoRepository cargoRepository, FuncionarioRepository funcionarioRepository, ModelMapper modelMapper) {
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CargoResponseDTO criarCargo(CargoRequestDTO requestDTO) {
        // Validação de negócio: não permitir criar um cargo que já existe
        cargoRepository.findByCargo(requestDTO.getCargo())
                .ifPresent(cargo -> {
                    throw new IllegalArgumentException("Este tipo de cargo já existe.");
                });

        Cargo novoCargo = modelMapper.map(requestDTO, Cargo.class);
        Cargo cargoSalvo = cargoRepository.save(novoCargo);
        return modelMapper.map(cargoSalvo, CargoResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CargoResponseDTO> listarTodos() {
        return cargoRepository.findAll().stream()
                .map(cargo -> modelMapper.map(cargo, CargoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CargoResponseDTO buscarPorId(Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cargo não encontrado com o ID: " + id));
        return modelMapper.map(cargo, CargoResponseDTO.class);
    }

    @Transactional
    public CargoResponseDTO atualizarCargo(Long id, CargoRequestDTO requestDTO) {
        Cargo cargoExistente = cargoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cargo não encontrado para atualização com o ID: " + id));

        // Verifica se o novo nome de cargo já está em uso por outro cargo
        cargoRepository.findByCargo(requestDTO.getCargo())
                .ifPresent(cargoEncontrado -> {
                    if (!cargoEncontrado.getId().equals(id)) {
                        throw new IllegalArgumentException("Este tipo de cargo já está em uso por outro registro.");
                    }
                });

        modelMapper.map(requestDTO, cargoExistente);
        cargoExistente.setId(id); // Garante que o ID não seja alterado

        Cargo cargoAtualizado = cargoRepository.save(cargoExistente);
        return modelMapper.map(cargoAtualizado, CargoResponseDTO.class);
    }

    @Transactional
    public void deletarCargo(Long id) {
        if (!cargoRepository.existsById(id)) {
            throw new NoSuchElementException("Cargo não encontrado para deleção com o ID: " + id);
        }

        //  não permitir deletar um cargo se existem funcionários associados a ele.
        if (funcionarioRepository.existsByCargoId(id)) {
            throw new IllegalStateException("Não é possível deletar o cargo, pois existem funcionários associados a ele.");
        }

        cargoRepository.deleteById(id);
    }
}