package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumViaMedicamento;
import br.com.clinicavet.clinica_api.domain.repository.MedicamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MedicamentoServiceImplementTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ModelMapper modelMapper;

    private MedicamentoServiceImplement medicamentoService;

    private Medicamento medicamento;
    private Produto produto;
    private MedicamentoRequestDTO requestDTO;
    private MedicamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        when(modelMapper.getConfiguration()).thenReturn(mock(Configuration.class));
        medicamentoService = new MedicamentoServiceImplement(medicamentoRepository, produtoRepository, modelMapper);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Amoxicilina");
        produto.setDescricao("Antibiótico de amplo espectro");
        produto.inicializarEstoque(50);

        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setProduto(produto);
        medicamento.setCategoria(EnumCategoriaMedicameno.ANTIBIOTICO);
        medicamento.setViaAdministracao(EnumViaMedicamento.ORAL);
        medicamento.setDosagemPadrao("500mg");
        medicamento.setPrincipioAtivo("Amoxicilina");
        medicamento.setPrescricaoObrigatoria(true);

        requestDTO = new MedicamentoRequestDTO();
        requestDTO.setNome("Amoxicilina");
        requestDTO.setDescricao("Antibiótico de amplo espectro");
        requestDTO.setQuantidadeEstoque(50);
        requestDTO.setCategoria(EnumCategoriaMedicameno.ANTIBIOTICO);
        requestDTO.setViaAdministracao(EnumViaMedicamento.ORAL);
        requestDTO.setDosagemPadrao("500mg");
        requestDTO.setPrincipioAtivo("Amoxicilina");
        requestDTO.setPrescricaoObrigatoria(true);

        responseDTO = new MedicamentoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Amoxicilina");
        responseDTO.setDescricao("Antibiótico de amplo espectro");
        responseDTO.setQuantidadeEstoque(50);
        responseDTO.setCategoria(EnumCategoriaMedicameno.ANTIBIOTICO);
    }

    @Test
    void criar_Sucesso() {
        when(produtoRepository.findByNomeIgnoreCase(requestDTO.getNome())).thenReturn(Optional.empty());
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamento);
        when(modelMapper.map(medicamento, MedicamentoResponseDTO.class)).thenReturn(responseDTO);

        MedicamentoResponseDTO resultado = medicamentoService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getNome());
        assertEquals(50, resultado.getQuantidadeEstoque());
        verify(produtoRepository, times(1)).findByNomeIgnoreCase(requestDTO.getNome());
        verify(medicamentoRepository, times(1)).save(any(Medicamento.class));
    }

    @Test
    void criar_ExceptionProdutoJaExiste() {
        when(produtoRepository.findByNomeIgnoreCase(requestDTO.getNome())).thenReturn(Optional.of(produto));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            medicamentoService.criar(requestDTO);
        });

        assertEquals("Já existe um produto cadastrado com o nome: Amoxicilina", exception.getMessage());
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(produtoRepository.findByNomeIgnoreCase(requestDTO.getNome())).thenReturn(Optional.of(produto));
        when(medicamentoRepository.save(medicamento)).thenReturn(medicamento);
        when(modelMapper.map(medicamento, MedicamentoResponseDTO.class)).thenReturn(responseDTO);

        MedicamentoResponseDTO resultado = medicamentoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getNome());
        verify(medicamentoRepository, times(1)).findById(1L);
        verify(medicamentoRepository, times(1)).save(medicamento);
    }

    @Test
    void atualizar_ExceptionMedicamentoNaoEncontrado() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            medicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("Medicamento não encontrado com o ID: 1", exception.getMessage());
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    void atualizar_ExceptionNomeJaEmUsoPorOutroProduto() {
        Produto outroProduto = new Produto();
        outroProduto.setId(2L);
        outroProduto.setNome("Dipirona");
        outroProduto.inicializarEstoque(10);

        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(produtoRepository.findByNomeIgnoreCase(requestDTO.getNome())).thenReturn(Optional.of(outroProduto));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            medicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("O nome 'Amoxicilina' já está em uso por outro produto.", exception.getMessage());
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    void atualizar_SucessoComNomeNulo() {
        requestDTO.setNome(null);
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(medicamentoRepository.save(medicamento)).thenReturn(medicamento);
        when(modelMapper.map(medicamento, MedicamentoResponseDTO.class)).thenReturn(responseDTO);

        MedicamentoResponseDTO resultado = medicamentoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        verify(produtoRepository, never()).findByNomeIgnoreCase(any());
        verify(medicamentoRepository, times(1)).save(medicamento);
    }

    @Test
    void atualizar_SucessoComNomeNovoSemConflito() {
        requestDTO.setNome("Paracetamol");
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(produtoRepository.findByNomeIgnoreCase("Paracetamol")).thenReturn(Optional.empty());
        when(medicamentoRepository.save(medicamento)).thenReturn(medicamento);
        when(modelMapper.map(medicamento, MedicamentoResponseDTO.class)).thenReturn(responseDTO);

        MedicamentoResponseDTO resultado = medicamentoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        verify(medicamentoRepository, times(1)).save(medicamento);
    }

    @Test
    void mapEntidadeParaResponse_SemProdutoAssociado() throws Exception {
        Medicamento medicamentoSemProduto = new Medicamento();
        medicamentoSemProduto.setId(9L);
        MedicamentoResponseDTO dtoBase = new MedicamentoResponseDTO();
        when(modelMapper.map(medicamentoSemProduto, MedicamentoResponseDTO.class)).thenReturn(dtoBase);

        java.lang.reflect.Method method = MedicamentoServiceImplement.class.getDeclaredMethod("mapEntidadeParaResponse", Medicamento.class);
        method.setAccessible(true);
        MedicamentoResponseDTO resultado = (MedicamentoResponseDTO) method.invoke(medicamentoService, medicamentoSemProduto);

        assertNotNull(resultado);
        assertNull(resultado.getNome());
    }
}
