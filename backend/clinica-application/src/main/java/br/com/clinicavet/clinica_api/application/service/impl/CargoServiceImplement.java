package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;

import br.com.clinicavet.clinica_api.application.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.application.service.CargoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;


@Service
public class CargoServiceImplement extends GenericCrudServiceImplement<Cargo, Long, CargoRequestDTO, CargoResponseDTO> implements CargoService {

    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ModelMapper modelMapper;

    public CargoServiceImplement(CargoRepository cargoRepository, FuncionarioRepository funcionarioRepository, ModelMapper modelMapper) {
        super(cargoRepository, modelMapper);
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CargoResponseDTO criar(CargoRequestDTO requestDTO) {
        cargoRepository.findByCargo(requestDTO.getCargo())
                .ifPresent(cargo -> {
                    throw new IllegalArgumentException("Este tipo de cargo já existe.");
                });

        Cargo novoCargo = modelMapper.map(requestDTO, Cargo.class);
        Cargo cargoSalvo = cargoRepository.save(novoCargo);
        return modelMapper.map(cargoSalvo, CargoResponseDTO.class);
    }

    @Override
    @Transactional
    public CargoResponseDTO atualizar(Long id, CargoRequestDTO requestDTO) {
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
    @Override
    @Transactional
    public void deletar(Long id) {
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
