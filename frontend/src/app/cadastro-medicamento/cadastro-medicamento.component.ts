import { Component, EventEmitter, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MedicamentoService } from '../services/medicamento.service';
import { MedicamentoRequest } from '../models/medicamento.model';

@Component({
  selector: 'app-cadastro-medicamento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro-medicamento.component.html',
  styleUrls: ['./cadastro-medicamento.component.css']
})
export class CadastroMedicamentoComponent implements OnInit {
  @Output() voltar = new EventEmitter<void>();

  novoMedicamento: MedicamentoRequest = {
    nome: '',
    quantidadeEstoque: 0,
    categoria: '',
    prescricaoObrigatoria: false,
    descricao: '',
    viaAdministracao: '',
    dosagemPadrao: '',
    principioAtivo: '',
    fabricante: ''
  };

  categorias: string[] = [];
  viasDeAdministracao: string[] = [];
  successMessage: string | null = null;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private medicamentoService: MedicamentoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDropdownData();
  }

  loadDropdownData(): void {
    this.medicamentoService.getCategorias().subscribe({
      next: (data) => { this.categorias = data; },
      error: (err) => { this.errorMessage = 'Não foi possível carregar as categorias.'; console.error(err); }
    });

    this.medicamentoService.getViasAdministracao().subscribe({
      next: (data) => { this.viasDeAdministracao = data; },
      error: (err) => { this.errorMessage = 'Não foi possível carregar as vias de administração.'; console.error(err); }
    });
  }

  onSubmit(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;


    const request = { ...this.novoMedicamento };
    if (!request.descricao) delete request.descricao;
    if (!request.viaAdministracao) delete request.viaAdministracao;
    if (!request.dosagemPadrao) delete request.dosagemPadrao;
    if (!request.principioAtivo) delete request.principioAtivo;
    if (!request.fabricante) delete request.fabricante;

    this.medicamentoService.createMedicamento(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = `Medicamento "${response.nome}" cadastrado com sucesso!`;
        this.resetForm();
        this.cdr.detectChanges();

        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 4000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Ocorreu um erro ao cadastrar o medicamento.';
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }

  resetForm(): void {
    this.novoMedicamento = {
      nome: '', quantidadeEstoque: 0, categoria: '', prescricaoObrigatoria: false,
      descricao: '', viaAdministracao: '', dosagemPadrao: '', principioAtivo: '', fabricante: ''
    };
  }
}
