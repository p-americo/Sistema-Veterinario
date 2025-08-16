package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.RegistroProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.RegistroProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.model.*;
import br.com.clinicavet.clinica_api.repository.*;
import br.com.clinicavet.clinica_api.service.Interface.RegistroProntuarioServiceInterface;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RegistroProntuarioServiceImplement implements RegistroProntuarioServiceInterface {

    private final RegistroProntuarioRepository registroProntuarioRepository;
    private final ProntuarioRepository prontuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final InternacaoRepository internacaoRepository;
    private final ModelMapper modelMapper;

    public RegistroProntuarioServiceImplement(RegistroProntuarioRepository registroProntuarioRepository, ProntuarioRepository prontuarioRepository, FuncionarioRepository funcionarioRepository, AgendamentoRepository agendamentoRepository, InternacaoRepository internacaoRepository, ModelMapper modelMapper) {
        this.registroProntuarioRepository = registroProntuarioRepository;
        this.prontuarioRepository = prontuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.internacaoRepository = internacaoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public RegistroProntuarioResponseDTO criarRegistro(RegistroProntuarioRequestDTO registroRequestDTO) {
        Prontuario prontuario = prontuarioRepository.findById(registroRequestDTO.getProntuarioId())
                .orElseThrow(() -> new NoSuchElementException("Prontuário não encontrado com o ID: " + registroRequestDTO.getProntuarioId()));

        Funcionario veterinario = funcionarioRepository.findById(registroRequestDTO.getVeterinarioResponsavelId())
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + registroRequestDTO.getVeterinarioResponsavelId()));

        RegistroProntuario novoRegistro = new RegistroProntuario();
        novoRegistro.setId(null);
        novoRegistro.setDataHora(registroRequestDTO.getDataHora());
        novoRegistro.setPesoNoDia(registroRequestDTO.getPesoNoDia());
        novoRegistro.setObservacoesClinicas(registroRequestDTO.getObservacoesClinicas());
        novoRegistro.setDiagnostico(registroRequestDTO.getDiagnostico());
        // --- FIM DA CORREÇÃO ---

        novoRegistro.setProntuario(prontuario);
        novoRegistro.setVeterinarioResponsavel(veterinario);

        if (registroRequestDTO.getAgendamentoId() != null) {
            Agendamento agendamento = agendamentoRepository.findById(registroRequestDTO.getAgendamentoId())
                    .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com o ID: " + registroRequestDTO.getAgendamentoId()));
            novoRegistro.setAgendamento(agendamento);
        }

        if (registroRequestDTO.getInternacaoId() != null) {
            Internacao internacao = internacaoRepository.findById(registroRequestDTO.getInternacaoId())
                    .orElseThrow(() -> new NoSuchElementException("Internação não encontrada com o ID: " + registroRequestDTO.getInternacaoId()));
            novoRegistro.setInternacao(internacao);
        }

        RegistroProntuario registroSalvo = registroProntuarioRepository.save(novoRegistro);

        return mapEntidadeParaResponse(registroSalvo);
    }

    @Override
    @Transactional
    public RegistroProntuarioResponseDTO atualizarRegistro(Long id, RegistroProntuarioRequestDTO registroRequestDTO) {
        RegistroProntuario registroExistente = registroProntuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Registro do prontuário não encontrado com o ID: " + id));

        modelMapper.map(registroRequestDTO, registroExistente);

        if (registroRequestDTO.getVeterinarioResponsavelId() != null) {
            Funcionario veterinario = funcionarioRepository.findById(registroRequestDTO.getVeterinarioResponsavelId())
                    .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + registroRequestDTO.getVeterinarioResponsavelId()));
            registroExistente.setVeterinarioResponsavel(veterinario);
        }

        if (registroRequestDTO.getAgendamentoId() != null) {
            Agendamento agendamento = agendamentoRepository.findById(registroRequestDTO.getAgendamentoId())
                    .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com o ID: " + registroRequestDTO.getAgendamentoId()));
            registroExistente.setAgendamento(agendamento);
        }

        if (registroRequestDTO.getInternacaoId() != null) {
            Internacao internacao = internacaoRepository.findById(registroRequestDTO.getInternacaoId())
                    .orElseThrow(() -> new NoSuchElementException("Internação não encontrada com o ID: " + registroRequestDTO.getInternacaoId()));
            registroExistente.setInternacao(internacao);
        }

        RegistroProntuario registroAtualizado = registroProntuarioRepository.save(registroExistente);
        return mapEntidadeParaResponse(registroAtualizado);
    }

    @Override
    @Transactional
    public void deletarRegistro(Long id) {
        if (!registroProntuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Registro do prontuário não encontrado com o ID: " + id);
        }
        registroProntuarioRepository.deleteById(id);
    }

    @Override
    public RegistroProntuarioResponseDTO buscarPorId(Long id) {
        RegistroProntuario registro = registroProntuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Registro do prontuário não encontrado com o ID: " + id));
        return mapEntidadeParaResponse(registro);
    }

    @Override
    public List<RegistroProntuarioResponseDTO> buscarPorProntuarioId(Long prontuarioId) {
        return registroProntuarioRepository.findByProntuarioIdOrderByDataHoraDesc(prontuarioId).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroProntuarioResponseDTO> buscarPorVeterinarioId(Long veterinarioId) {
        return registroProntuarioRepository.findByVeterinarioResponsavelId(veterinarioId).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroProntuarioResponseDTO> buscarPorInternacaoId(Long internacaoId) {
        return registroProntuarioRepository.findByInternacaoId(internacaoId).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroProntuarioResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return registroProntuarioRepository.findByDataHoraBetween(inicio, fim).stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroProntuarioResponseDTO> listarTodos() {
        return registroProntuarioRepository.findAll().stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RegistroProntuarioResponseDTO buscarPorIdComMedicamentos(Long id) {
        RegistroProntuario registro = registroProntuarioRepository.findByIdWithMedicamentos(id);
        if (registro == null) {
            throw new NoSuchElementException("Registro do prontuário não encontrado com o ID: " + id);
        }

        RegistroProntuarioResponseDTO response = mapEntidadeParaResponse(registro);

        if (registro.getMedicamentosAdministrados() != null) {
            List<AdministracaoMedicamentoResponseDTO> medicamentos = registro.getMedicamentosAdministrados().stream()
                    .map(administracao -> {
                        AdministracaoMedicamentoResponseDTO dto = modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class);
                        if (administracao.getMedicamento() != null && administracao.getMedicamento().getProduto() != null) {
                            dto.setNomeMedicamento(administracao.getMedicamento().getProduto().getNome());
                        }
                        if (administracao.getFuncionarioExecutor() != null) {
                            dto.setNomeFuncionarioExecutor(administracao.getFuncionarioExecutor().getNome());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            response.setMedicamentosAdministrados(medicamentos);
        }

        return response;
    }

    private RegistroProntuarioResponseDTO mapEntidadeParaResponse(RegistroProntuario registro) {
        RegistroProntuarioResponseDTO dto = new RegistroProntuarioResponseDTO();

        dto.setId(registro.getId());
        dto.setDataHora(registro.getDataHora());
        dto.setPesoNoDia(registro.getPesoNoDia());
        dto.setObservacoesClinicas(registro.getObservacoesClinicas());
        dto.setDiagnostico(registro.getDiagnostico());

        if (registro.getProntuario() != null) {
            dto.setProntuarioId(registro.getProntuario().getId());
        }

        if (registro.getAgendamento() != null) {
            dto.setAgendamentoId(registro.getAgendamento().getId());
        }

        if (registro.getVeterinarioResponsavel() != null) {
            dto.setVeterinarioResponsavelId(registro.getVeterinarioResponsavel().getId());
            dto.setNomeVeterinario(registro.getVeterinarioResponsavel().getNome()); // A linha mais importante
        }

        if (registro.getInternacao() != null) {
            dto.setInternacaoId(registro.getInternacao().getId());
        }
        return dto;
    }
}