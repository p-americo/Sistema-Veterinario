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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.NoSuchElementException;


@Service
public class AnimalServiceImplement extends BaseServiceImplement< Animal, Long, AnimalRequestDTO, AnimalResponseDTO> implements AnimalService {

    private final AnimalRepository animalRepository;
    private final ModelMapper modelMapper;
    private final ClienteRepository clienteRepository;


    public AnimalServiceImplement(AnimalRepository animalRepository, ModelMapper modelMapper, ClienteRepository clienteRepository) {
        super(animalRepository, modelMapper);
        this.animalRepository = animalRepository;
        this.modelMapper = modelMapper;
        this.clienteRepository = clienteRepository;
        this.modelMapper.getConfiguration().setSkipNullEnabled(true);
    }


    @Transactional
    public AnimalResponseDTO criar(AnimalRequestDTO animalDTO, MultipartFile arquivoImagem) throws IOException {
        // Validações Iniciais
        Cliente cliente = clienteRepository.findById(animalDTO.getClienteId())
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com o ID: " + animalDTO.getClienteId()));

        if (animalRepository.existsByNome(animalDTO.getNome()) && animalRepository.existsByClienteId(animalDTO.getClienteId())) {
            throw new DataIntegrityViolationException("Animal ja cadastrado no mesmo cliente");
        }


        Animal novoAnimal = new Animal();
        novoAnimal.setNome(animalDTO.getNome());
        novoAnimal.setEspecie(animalDTO.getEspecie());
        novoAnimal.setPorte(animalDTO.getPorte());
        novoAnimal.setRaca(animalDTO.getRaca());
        novoAnimal.setSexo(animalDTO.getSexo());
        novoAnimal.setCor(animalDTO.getCor());
        novoAnimal.setPeso(animalDTO.getPeso().doubleValue());
        novoAnimal.setCastrado(animalDTO.getCastrado());
        novoAnimal.setDataNascimento(animalDTO.getDataNascimento());
        novoAnimal.setObservacao(animalDTO.getObservacao());
        novoAnimal.setCliente(cliente);


        if (arquivoImagem != null && !arquivoImagem.isEmpty()) {
            novoAnimal.setImagem(arquivoImagem.getBytes());
        }

        Animal animalSalvo = animalRepository.save(novoAnimal);

        return mapEntidadeParaResponse(animalSalvo);
    }

    @Override
    @Transactional
    public byte[] buscarImagemPorIdAnimal(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com o ID: " + id));
        return animal.getImagem();
    }


    @Override
    public AnimalResponseDTO atualizar(Long animalId, AnimalRequestDTO animalDTO) {
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