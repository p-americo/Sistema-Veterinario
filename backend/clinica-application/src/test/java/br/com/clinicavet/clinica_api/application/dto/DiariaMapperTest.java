package br.com.clinicavet.clinica_api.application.dto;

import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiariaMapperTest {

    @Test
    void toEntity_ComDtoNulo_DeveRetornarNulo() {
        assertNull(DiariaMapper.toEntity(null));
    }

    @Test
    void toEntity_ComDtoValido_DeveMapearCampos() {
        DiariaRequestDTO dto = new DiariaRequestDTO();
        dto.setDataHora(LocalDateTime.of(2026, 6, 23, 10, 0));
        dto.setDiagnostico("Em recuperação");
        dto.setObservacoesClinicas("Animal estável");
        dto.setPesoNoDia(new BigDecimal("15.5"));

        DiariaInternacao entity = DiariaMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getDataHora(), entity.getDataHora());
        assertEquals(dto.getDiagnostico(), entity.getDiagnostico());
        assertEquals(dto.getObservacoesClinicas(), entity.getObservacoesClinicas());
        assertEquals(dto.getPesoNoDia(), entity.getPesoNoDia());
    }

    @Test
    void toResponseDTO_ComEntidadeNula_DeveRetornarNulo() {
        assertNull(DiariaMapper.toResponseDTO(null));
    }

    @Test
    void toResponseDTO_ComEntidadeSemMedicamentos_DeveRetornarListaVaziaDeMedicamentos() {
        DiariaInternacao entity = new DiariaInternacao();
        entity.setId(1L);
        entity.setDataHora(LocalDateTime.of(2026, 6, 23, 10, 0));
        entity.setDiagnostico("Em recuperação");
        entity.setObservacoesClinicas("Animal estável");
        entity.setPesoNoDia(new BigDecimal("15.5"));

        DiariaResponseDTO dto = DiariaMapper.toResponseDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getDataHora(), dto.getDataHora());
        assertEquals(entity.getDiagnostico(), dto.getDiagnostico());
        assertNotNull(dto.getMedicamentos());
        assertTrue(dto.getMedicamentos().isEmpty());
    }

    @Test
    void toResponseDTO_ComMedicamentosNulos_DeveRetornarListaVazia() {
        DiariaInternacao entity = new DiariaInternacao();
        entity.setId(2L);
        entity.setMedicamentos(null);

        DiariaResponseDTO dto = DiariaMapper.toResponseDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getMedicamentos());
        assertTrue(dto.getMedicamentos().isEmpty());
    }

    @Test
    void toResponseDTO_ComMedicamentosCompletosEMinimosENulos_Sucesso() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(10L);
        Produto produto = new Produto();
        produto.setNome("Dipirona");
        produto.inicializarEstoque(50);
        medicamento.setProduto(produto);

        Funcionario funcionarioExecutor = new Funcionario();
        funcionarioExecutor.setId(20L);
        funcionarioExecutor.setNome("Dra. Ana");

        RegistroProntuario entradaProntuario = new RegistroProntuario();
        entradaProntuario.setId(30L);

        DiariaInternacao diariaParaAssociacao = new DiariaInternacao();
        diariaParaAssociacao.setId(40L);

        AdministracaoMedicamento administracaoCompleta = new AdministracaoMedicamento();
        administracaoCompleta.setId(50L);
        administracaoCompleta.registrarAdministracao(medicamento, funcionarioExecutor, BigDecimal.ONE, LocalDateTime.now(), "5mg");
        administracaoCompleta.associarAoProntuario(entradaProntuario);
        administracaoCompleta.associarDiaria(diariaParaAssociacao);

        AdministracaoMedicamento administracaoMinima = new AdministracaoMedicamento(
                51L, null, null, null, BigDecimal.ONE, LocalDateTime.now(), "5mg", null);

        Medicamento medicamentoSemProduto = new Medicamento();
        medicamentoSemProduto.setId(11L);
        AdministracaoMedicamento administracaoComMedSemProduto = new AdministracaoMedicamento();
        administracaoComMedSemProduto.setId(52L);
        administracaoComMedSemProduto.registrarAdministracao(medicamentoSemProduto, funcionarioExecutor, BigDecimal.ONE, LocalDateTime.now(), "5mg");

        List<AdministracaoMedicamento> medicamentos = new ArrayList<>();
        medicamentos.add(null);
        medicamentos.add(administracaoCompleta);
        medicamentos.add(administracaoMinima);
        medicamentos.add(administracaoComMedSemProduto);

        DiariaInternacao entity = new DiariaInternacao();
        entity.setId(60L);
        entity.setMedicamentos(medicamentos);

        DiariaResponseDTO dto = DiariaMapper.toResponseDTO(entity);

        assertNotNull(dto);
        assertEquals(4, dto.getMedicamentos().size());
        assertNull(dto.getMedicamentos().get(0));

        AdministracaoMedicamentoResponseDTO completaDTO = dto.getMedicamentos().get(1);
        assertEquals(20L, completaDTO.getFuncionarioExecutorId());
        assertEquals("Dra. Ana", completaDTO.getNomeFuncionarioExecutor());
        assertEquals(10L, completaDTO.getMedicamentoId());
        assertEquals("Dipirona", completaDTO.getNomeMedicamento());
        assertEquals(30L, completaDTO.getEntradaProntuarioId());
        assertEquals(40L, completaDTO.getDiariaId());

        AdministracaoMedicamentoResponseDTO minimaDTO = dto.getMedicamentos().get(2);
        assertNull(minimaDTO.getMedicamentoId());
        assertNull(minimaDTO.getNomeMedicamento());
        assertNull(minimaDTO.getEntradaProntuarioId());
        assertNull(minimaDTO.getDiariaId());
        assertNull(minimaDTO.getFuncionarioExecutorId());

        AdministracaoMedicamentoResponseDTO medSemProdutoDTO = dto.getMedicamentos().get(3);
        assertEquals(11L, medSemProdutoDTO.getMedicamentoId());
        assertNull(medSemProdutoDTO.getNomeMedicamento());
    }

    @Test
    void construtor_DeveSerInstanciavel() {
        assertNotNull(new DiariaMapper());
    }
}
