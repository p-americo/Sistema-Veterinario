package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;


import br.com.clinicavet.clinica_api.application.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AgendamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.*; // EAnimal, ECliente, EServico, EAgendamento
import br.com.clinicavet.clinica_api.domain.model.enums.EnumAgendamento; // Importe seu Enum de Status
import br.com.clinicavet.clinica_api.domain.repository.*; // Todos os repositórios
import br.com.clinicavet.clinica_api.application.service.AgendamentoService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AgendamentoServiceImplement extends GenericCrudServiceImplement<Agendamento, Long, AgendamentoRequestDTO, AgendamentoResponseDTO> implements AgendamentoService {

    // @Autowired faz a injeção das dependecias na classe, caso delcare somente em cima do atributo
    // a não sem construtor o atributo não pode ser final = mutavel
    // indicar no construtor, antes se tinha mais contruttores,
    private final AgendamentoRepository agendamentoRepository;
    private final AnimalRepository animalRepository;
    private final ServicoRepository servicoRepository;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;


    public AgendamentoServiceImplement(AgendamentoRepository agendamentoRepository, AnimalRepository animalRepository, ServicoRepository servicoRepository, ClienteRepository clienteRepository, ModelMapper modelMapper) {
       super(agendamentoRepository, modelMapper);
        this.agendamentoRepository = agendamentoRepository;
        this.animalRepository = animalRepository;
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO requestDTO) {
        log.info("Tentando criar agendamento para o Cliente ID: {}, Animal ID: {}, Serviço ID: {} no horário: {}", 
                requestDTO.getClienteId(), requestDTO.getAnimalId(), requestDTO.getServicoId(), requestDTO.getDataHoraAgendamento());
        // Busca as entidades relacionadas
        Cliente cliente = clienteRepository.findById(requestDTO.getClienteId()).orElseThrow(() -> {
            log.warn("Falha ao criar agendamento: Cliente ID {} não encontrado", requestDTO.getClienteId());
            return new NoSuchElementException("Cliente não encontrado");
        });
        Animal animal = animalRepository.findById(requestDTO.getAnimalId()).orElseThrow(() -> {
            log.warn("Falha ao criar agendamento: Animal ID {} não encontrado", requestDTO.getAnimalId());
            return new NoSuchElementException("Animal não encontrado");
        });
        Servico servico = servicoRepository.findById(requestDTO.getServicoId()).orElseThrow(() -> {
            log.warn("Falha ao criar agendamento: Serviço ID {} não encontrado", requestDTO.getServicoId());
            return new NoSuchElementException("Serviço não encontrado");
        });

        Agendamento novoAgendamento = new Agendamento();
        novoAgendamento.associarClienteEAnimal(cliente, animal);
        novoAgendamento.setServico(servico);
        novoAgendamento.setDataHoraAgendamento(requestDTO.getDataHoraAgendamento());
        novoAgendamento.setObservacoes(requestDTO.getObservacoes());
        
        Agendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);
        log.info("Agendamento criado com sucesso com ID: {} e status: {}", agendamentoSalvo.getId(), agendamentoSalvo.getStatus());

        return modelMapper.map(agendamentoSalvo, AgendamentoResponseDTO.class);
    }


    @Transactional
    public AgendamentoResponseDTO cancelarAgendamento(Long id) {
        log.info("Cancelando agendamento com ID: {}", id);
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao cancelar: Agendamento não encontrado com o ID: {}", id);
                    return new NoSuchElementException("Agendamento não encontrado com o ID: " + id);
                });

        agendamento.cancelar();
        agendamentoRepository.save(agendamento);
        log.info("Agendamento ID: {} cancelado com sucesso.", id);
        return modelMapper.map(agendamento, AgendamentoResponseDTO.class);
    }


    @Transactional(readOnly = true)
    public AgendamentoResponseDTO listarPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com o ID: " + id));
        return modelMapper.map(agendamento, AgendamentoResponseDTO.class);
    }


    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listarTodos() {
        return agendamentoRepository.findAll().stream()
                .map(agendamento -> modelMapper.map(agendamento, AgendamentoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public AgendamentoResponseDTO confirmarAgendamento(Long id) {
        log.info("Confirmando agendamento com ID: {}", id);
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao confirmar: Agendamento não encontrado com o ID: {}", id);
                    return new NoSuchElementException("Agendamento não encontrado com o ID: " + id);
                });

        agendamento.confirmar();
        agendamentoRepository.save(agendamento);
        log.info("Agendamento ID: {} confirmado com sucesso.", id);

        return modelMapper.map(agendamento, AgendamentoResponseDTO.class);
    }

    @Transactional
    public AgendamentoResponseDTO atualizarAgendamento(Long id, AgendamentoRequestDTO updateDTO) {
        Agendamento agendamentoExistente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com o ID: " + id));

        // Valida estado e atualiza dados básicos
        agendamentoExistente.atualizarDados(updateDTO.getDataHoraAgendamento(), updateDTO.getObservacoes());

        // Se o cliente ou animal mudaram, valida a nova associação
        if (!agendamentoExistente.getCliente().getId().equals(updateDTO.getClienteId()) ||
            !agendamentoExistente.getAnimal().getId().equals(updateDTO.getAnimalId())) {
            
            Cliente cliente = clienteRepository.findById(updateDTO.getClienteId())
                    .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
            Animal animal = animalRepository.findById(updateDTO.getAnimalId())
                    .orElseThrow(() -> new NoSuchElementException("Animal não encontrado"));
            
            agendamentoExistente.associarClienteEAnimal(cliente, animal);
        }

        // Se o serviço mudou, atualiza
        if (!agendamentoExistente.getServico().getId().equals(updateDTO.getServicoId())) {
            Servico servico = servicoRepository.findById(updateDTO.getServicoId())
                    .orElseThrow(() -> new NoSuchElementException("Serviço não encontrado"));
            agendamentoExistente.setServico(servico);
        }

        Agendamento agendamentoAtualizado = agendamentoRepository.save(agendamentoExistente);

        return modelMapper.map(agendamentoAtualizado, AgendamentoResponseDTO.class);
    }

    @Transactional
    public AgendamentoResponseDTO realizarAgendamento(Long id) {
        log.info("Realizando atendimento do agendamento com ID: {}", id);
        Agendamento realizarAgendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao realizar: Agendamento não encontrado com o ID: {}", id);
                    return new NoSuchElementException("Agendamento não encontrado com o ID: " + id);
                });

        realizarAgendamento.realizar();
        agendamentoRepository.save(realizarAgendamento);
        log.info("Agendamento ID: {} realizado com sucesso.", id);
        return modelMapper.map(realizarAgendamento, AgendamentoResponseDTO.class);
    }

    @Transactional
    public void deletarAgendamento(Long id) {
        log.info("Deletando agendamento com ID: {}", id);
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao deletar: Agendamento não encontrado com o ID: {}", id);
                    return new NoSuchElementException("Agendamento não encontrado com o ID: " + id);
                });
        agendamentoRepository.delete(agendamento);
        log.info("Agendamento ID: {} deletado com sucesso.", id);
    }
}