package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.model.Animal;
import br.com.clinicavet.clinica_api.model.Cliente;
import br.com.clinicavet.clinica_api.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.service.Interface.AnimalService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AnimalServiceImplement implements AnimalService {

    private final AnimalRepository animalRepository;
    private final ModelMapper modelMapper;
    private final ClienteRepository clienteRepository;


    public AnimalServiceImplement(AnimalRepository animalRepository, ModelMapper modelMapper, ClienteRepository clienteRepository) {
        this.animalRepository = animalRepository;
        this.modelMapper = modelMapper;
        this.clienteRepository = clienteRepository;
        this.modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    @Transactional
    public AnimalResponseDTO criarAnimal(AnimalRequestDTO animalDTO) {
        Cliente cliente = clienteRepository.findById(animalDTO.getClienteId())
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com o ID: " + animalDTO.getClienteId()));

        if (animalRepository.existsByNome(animalDTO.getNome()) && animalRepository.existsByClienteId(animalDTO.getClienteId())) {
            throw new DataIntegrityViolationException("Animal ja cadastrado no mesmo cliente");
        }
        Animal novoAnimal = modelMapper.map(animalDTO, Animal.class);
        novoAnimal.setId(null);
        novoAnimal.setCliente(cliente);
        Animal animalSalvo = animalRepository.save(novoAnimal);
        return mapEntidadeParaResponse(animalSalvo);
    }

    @Transactional
    public AnimalResponseDTO buscarAnimalPorId(long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com o ID: " + animalId));
        return mapEntidadeParaResponse(animal);
    }


    @Override
    public List<AnimalResponseDTO> listarTodos() {
        return animalRepository.findAll().stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnimalResponseDTO atualizarAnimal(Long animalId, AnimalRequestDTO animalDTO) {
        Animal animalExistente = animalRepository.findById(animalId)
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com o ID: " + animalId));
        modelMapper.map(animalDTO, animalExistente);
        if (animalDTO.getClienteId() != null) {
            if (animalExistente.getCliente() == null || !animalDTO.getClienteId().equals(animalExistente.getCliente().getId())) {
                Cliente novoCliente = clienteRepository.findById(animalDTO.getClienteId())
                        .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com o ID: " + animalDTO.getClienteId()));
                animalExistente.setCliente(novoCliente);
            }
        }
        Animal animalAtualizado = animalRepository.save(animalExistente);
        return mapEntidadeParaResponse(animalAtualizado);
    }

    @Override
    @Transactional
    public void deletarAnimal(Long id) {
        animalRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Animal não encontrado com o ID: " + id));
        animalRepository.deleteById(id);
    }

    @Override
    public AnimalResponseDTO buscarPorId(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com o ID: " + id));
        return mapEntidadeParaResponse(animal);
    }


    private AnimalResponseDTO mapEntidadeParaResponse(Animal animal) {
        // Mapeia os campos básicos (nome, especie, etc.)
        AnimalResponseDTO dto = modelMapper.map(animal, AnimalResponseDTO.class);


        if (animal.getDataNascimento() != null) {
            Period periodo = Period.between(animal.getDataNascimento(), LocalDate.now());
            String idadeFormatada = String.format("%d anos e %d meses", periodo.getYears(), periodo.getMonths());
            dto.setIdadeFormatada(idadeFormatada);
        } else {
            dto.setIdadeFormatada("Idade não informada");
        }

        // ADIÇÃO CRÍTICA: Pega os dados do cliente e os coloca no DTO
        if (animal.getCliente() != null) {
            AnimalResponseDTO.ClienteInfo clienteInfo = new AnimalResponseDTO.ClienteInfo(
                    animal.getCliente().getId(),
                    animal.getCliente().getNome()
            );
            // Define este objeto no DTO de resposta
            dto.setCliente(clienteInfo);
        }

        return dto;
    }
}