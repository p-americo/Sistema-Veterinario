package br.com.clinicavet.clinica_api.model;

import br.com.clinicavet.clinica_api.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.model.enums.EnumSexo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "animais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false)
    private EnumEspecie especie;

   @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumPorte porte;

    @Column(length = 50, nullable = false)
    private String raca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumSexo sexo;

    @Column(length = 15)
    private String cor;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false)
    private Boolean castrado;

    @Column(name = "data_nascimento",  nullable = false)
    private LocalDate dataNascimento;

    @Column(length = 255)
    private String observacao;

    @OneToOne(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prontuario prontuario;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

}
