package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumAgendamento;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumViaMedicamento;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Enums", description = "Listagem de valores enumerados utilizados pela aplicação")
@RestController
@RequestMapping("/api/enums")
@CrossOrigin(origins = "http://localhost:4200")
public class EnumController {

    @GetMapping("/especies")
    public EnumEspecie[] getEspecies() {
        return EnumEspecie.values();
    }

    @GetMapping("/portes")
    public EnumPorte[] getPortes() {
        return EnumPorte.values();
    }

    @GetMapping("/sexos")
    public EnumSexo[] getSexos() {
        return EnumSexo.values();
    }

    @GetMapping("/agendamento-status")
    public EnumAgendamento[] getAgendamentoStatus() {
        return EnumAgendamento.values();
    }

    @GetMapping("/servico-tipos")
    public EnumServico[] getServicoTipos() {
        return EnumServico.values();
    }

    @GetMapping("/medicamento-categorias")
    public EnumCategoriaMedicameno[] getMedicamentoCategorias() {
        return EnumCategoriaMedicameno.values();
    }

    @GetMapping("/medicamento-vias")
    public EnumViaMedicamento[] getMedicamentoVias() {
        return EnumViaMedicamento.values();
    }

    @GetMapping("/internacao-status")
    public EnumInternacaoStatus[] getInternacaoStatus() {
        return EnumInternacaoStatus.values();
    }
}
