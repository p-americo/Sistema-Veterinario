package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;

import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import br.com.clinicavet.clinica_api.domain.repository.AdministracaoMedicamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.MedicamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.RegistroProntuarioRepository;
import br.com.clinicavet.clinica_api.application.service.AdminstracaoMedicamentoService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AdministracaoMedicamentoServiceImplement extends GenericCrudServiceImplement<AdministracaoMedicamento, Long, AdministracaoMedicamentoRequestDTO, AdministracaoMedicamentoResponseDTO > implements AdminstracaoMedicamentoService {

    private final AdministracaoMedicamentoRepository administracaoMedicamentoRepository;
    private final RegistroProntuarioRepository registroProntuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final InternacaoRepository internacaoRepository;
    private final ModelMapper modelMapper;

    public AdministracaoMedicamentoServiceImplement(AdministracaoMedicamentoRepository administracaoMedicamentoRepository, RegistroProntuarioRepository registroProntuarioRepository, FuncionarioRepository funcionarioRepository, MedicamentoRepository medicamentoRepository, InternacaoRepository internacaoRepository, ModelMapper modelMapper) {
        super(administracaoMedicamentoRepository, modelMapper);
        this.administracaoMedicamentoRepository = administracaoMedicamentoRepository;
        this.registroProntuarioRepository = registroProntuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.internacaoRepository = internacaoRepository;
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    @Override
    @Transactional
    public AdministracaoMedicamentoResponseDTO criar(AdministracaoMedicamentoRequestDTO dto) {
        if (dto.getEntradaProntuarioId() == null && dto.getDiariaId() == null) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("É necessário informar ou o ID da entrada do prontuário ou o ID da diária.");
        }

        Funcionario funcionarioExecutor = funcionarioRepository.findById(dto.getFuncionarioExecutorId())
                .orElseThrow(() -> new NoSuchElementException("Funcionário executor não encontrado com o ID: " + dto.getFuncionarioExecutorId()));

        Medicamento medicamento = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new NoSuchElementException("Medicamento não encontrado com o ID: " + dto.getMedicamentoId()));

        AdministracaoMedicamento novaAdministracao = new AdministracaoMedicamento();
        novaAdministracao.registrarAdministracao(medicamento, funcionarioExecutor, dto.getQuantidadeAdministrada(), dto.getDataHora(), dto.getDosagem());

        if (dto.getEntradaProntuarioId() != null) {
            RegistroProntuario entradaProntuario = registroProntuarioRepository.findById(dto.getEntradaProntuarioId())
                    .orElseThrow(() -> new NoSuchElementException("Entrada do prontuário não encontrada com o ID: " + dto.getEntradaProntuarioId()));
            novaAdministracao.associarAoProntuario(entradaProntuario);
        }

        if (dto.getDiariaId() != null) {
            DiariaInternacao diaria = internacaoRepository.findDiariaById(dto.getDiariaId())
                    .orElseThrow(() -> new NoSuchElementException("Diária de internação não encontrada com o ID: " + dto.getDiariaId()));
            novaAdministracao.associarDiaria(diaria);
        }

        AdministracaoMedicamento administracaoSalva = administracaoMedicamentoRepository.save(novaAdministracao);

        return mapEntidadeParaResponse(administracaoSalva);
    }

    @Override
    @Transactional
    public AdministracaoMedicamentoResponseDTO atualizar(Long id, AdministracaoMedicamentoRequestDTO dto) {
        if (dto.getEntradaProntuarioId() == null && dto.getDiariaId() == null) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("É necessário informar ou o ID da entrada do prontuário ou o ID da diária.");
        }

        AdministracaoMedicamento administracaoExistente = administracaoMedicamentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Administração de medicamento não encontrada com o ID: " + id));

        Funcionario funcionarioExecutor = funcionarioRepository.findById(dto.getFuncionarioExecutorId())
                .orElseThrow(() -> new NoSuchElementException("Funcionário executor não encontrado com o ID: " + dto.getFuncionarioExecutorId()));

        Medicamento medicamento = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new NoSuchElementException("Medicamento não encontrado com o ID: " + dto.getMedicamentoId()));

        // Atualiza os dados usando regras de negócio
        administracaoExistente.registrarAdministracao(medicamento, funcionarioExecutor, dto.getQuantidadeAdministrada(), dto.getDataHora(), dto.getDosagem());

        if (dto.getEntradaProntuarioId() != null) {
            RegistroProntuario entradaProntuario = registroProntuarioRepository.findById(dto.getEntradaProntuarioId())
                    .orElseThrow(() -> new NoSuchElementException("Entrada do prontuário não encontrada com o ID: " + dto.getEntradaProntuarioId()));
            administracaoExistente.associarAoProntuario(entradaProntuario);
        }

        if (dto.getDiariaId() != null) {
            DiariaInternacao diaria = internacaoRepository.findDiariaById(dto.getDiariaId())
                    .orElseThrow(() -> new NoSuchElementException("Diária de internação não encontrada com o ID: " + dto.getDiariaId()));
            administracaoExistente.associarDiaria(diaria);
        }

        AdministracaoMedicamento administracaoAtualizada = administracaoMedicamentoRepository.save(administracaoExistente);
        return mapEntidadeParaResponse(administracaoAtualizada);
    }



    @Override
    public List<AdministracaoMedicamentoResponseDTO> buscarPorEntradaProntuarioId(Long entradaProntuarioId) {
        return administracaoMedicamentoRepository.findByEntradaProntuarioId(entradaProntuarioId).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdministracaoMedicamentoResponseDTO> buscarPorMedicamentoId(Long medicamentoId) {
        return administracaoMedicamentoRepository.findByMedicamentoId(medicamentoId).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdministracaoMedicamentoResponseDTO> buscarPorFuncionarioId(Long funcionarioId) {
        return administracaoMedicamentoRepository.findByFuncionarioExecutorId(funcionarioId).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdministracaoMedicamentoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return administracaoMedicamentoRepository.findByDataHoraBetween(inicio, fim).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }


    private AdministracaoMedicamentoResponseDTO mapEntidadeParaResponse(AdministracaoMedicamento administracao) {
        AdministracaoMedicamentoResponseDTO dto = modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class);

        if (administracao.getMedicamento() != null && administracao.getMedicamento().getProduto() != null) {
            dto.setMedicamentoId(administracao.getMedicamento().getId());

            dto.setNomeMedicamento(administracao.getMedicamento().getProduto().getNome());
        }

        if (administracao.getEntradaProntuario() != null) {
            dto.setEntradaProntuarioId(administracao.getEntradaProntuario().getId());
        }

        if (administracao.getDiaria() != null) {
            dto.setDiariaId(administracao.getDiaria().getId());
        }

        if (administracao.getFuncionarioExecutor() != null) {
            dto.setFuncionarioExecutorId(administracao.getFuncionarioExecutor().getId());
            dto.setNomeFuncionarioExecutor(administracao.getFuncionarioExecutor().getNome());
        }

        return dto;
    }
}
