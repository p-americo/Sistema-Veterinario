package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoUpdateDTO;
import br.com.clinicavet.clinica_api.model.Medicamento;
import br.com.clinicavet.clinica_api.model.Produto;
import br.com.clinicavet.clinica_api.repository.MedicamentoRepository;
import br.com.clinicavet.clinica_api.repository.ProdutoRepository;
import br.com.clinicavet.clinica_api.service.Interface.MedicamentoService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentoServiceImplement implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final ProdutoRepository produtoRepository;
    private final ModelMapper modelMapper;

    public MedicamentoServiceImplement(MedicamentoRepository medicamentoRepository, ProdutoRepository produtoRepository, ModelMapper modelMapper) {
        this.medicamentoRepository = medicamentoRepository;
        this.produtoRepository = produtoRepository;
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO criarMedicamento(MedicamentoRequestDTO dto) {
        if (produtoRepository.findByNomeIgnoreCase(dto.getNome()).isPresent()) {
            throw new DataIntegrityViolationException("Já existe um produto cadastrado com o nome: " + dto.getNome());
        }


        Produto novoProduto = new Produto();
        novoProduto.setNome(dto.getNome());
        novoProduto.setDescricao(dto.getDescricao());
        novoProduto.setQuantidadeEstoque(dto.getQuantidadeEstoque());

        Medicamento novoMedicamento = new Medicamento();
        modelMapper.map(dto, novoMedicamento);
        novoMedicamento.setId(null);
        novoMedicamento.setProduto(novoProduto);

        Medicamento medicamentoSalvo = medicamentoRepository.save(novoMedicamento);



        return mapEntidadeParaResponse(medicamentoSalvo);
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO atualizarMedicamento(Long id, MedicamentoRequestDTO dto) {
        Medicamento medicamentoExistente = medicamentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento não encontrado com o ID: " + id));

        Produto produtoAssociado = medicamentoExistente.getProduto();

        if (dto.getNome() != null) {
            Optional<Produto> produtoComMesmoNome = produtoRepository.findByNomeIgnoreCase(dto.getNome());
            if (produtoComMesmoNome.isPresent() && !produtoComMesmoNome.get().getId().equals(produtoAssociado.getId())) {
                throw new DataIntegrityViolationException("O nome '" + dto.getNome() + "' já está em uso por outro produto.");
            }
        }

        modelMapper.map(dto, medicamentoExistente);
        modelMapper.map(dto, produtoAssociado);

        Medicamento medicamentoAtualizado = medicamentoRepository.save(medicamentoExistente);
        return mapEntidadeParaResponse(medicamentoAtualizado);
    }

    @Override
    @Transactional
    public void deletarMedicamento(Long id) {
        if (!medicamentoRepository.existsById(id)) {
            throw new NoSuchElementException("Medicamento não encontrado com o ID: " + id);
        }
        medicamentoRepository.deleteById(id);
    }

    @Override
    public MedicamentoResponseDTO buscarPorId(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento não encontrado com o ID: " + id));
        return mapEntidadeParaResponse(medicamento);
    }

    @Override
    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll().stream()
                .map(this::mapEntidadeParaResponse)
                .collect(Collectors.toList());
    }

    private MedicamentoResponseDTO mapEntidadeParaResponse(Medicamento medicamento) {
        MedicamentoResponseDTO dto = modelMapper.map(medicamento, MedicamentoResponseDTO.class);
        if (medicamento.getProduto() != null) {
            dto.setNome(medicamento.getProduto().getNome());
            dto.setDescricao(medicamento.getProduto().getDescricao());
            dto.setQuantidadeEstoque(medicamento.getProduto().getQuantidadeEstoque());
        }
        return dto;
    }
}
