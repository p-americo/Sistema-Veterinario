package br.com.clinicavet.clinica_api.model;

import br.com.clinicavet.clinica_api.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.model.enums.EnumViaMedicamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "medicamento")
@Getter
@Setter
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "produto_id", referencedColumnName = "id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumCategoriaMedicameno categoria;

    @Enumerated(EnumType.STRING)
    private EnumViaMedicamento viaAdministracao;

    private String dosagemPadrao;

    private String principioAtivo;

    private Boolean prescricaoObrigatoria;
}
