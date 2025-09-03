import { Component, EventEmitter, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServicoService } from '../services/servico.service';
import { ServicoRequest } from '../models/servico.model';
import { FuncionarioResponse } from '../models/funcionario.model';

@Component({
  selector: 'app-cadastro-servico',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro-servico.component.html',
  styleUrls: ['./cadastro-servico.component.css']
})
export class CadastroServicoComponent implements OnInit {
  @Output() voltar = new EventEmitter<void>();

  novoServico: ServicoRequest = {
    tipo: '',
    veterinarioId: 0,
    valor: 0
  };

  tiposDeServico: string[] = [];
  veterinarios: FuncionarioResponse[] = [];
  successMessage: string | null = null;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private servicoService: ServicoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDropdownData();
  }

  loadDropdownData(): void {

    this.servicoService.getServicoTipos().subscribe({
      next: (data) => { this.tiposDeServico = data; },
      error: (err) => {
        this.errorMessage = 'Não foi possível carregar os tipos de serviço.';
        console.error(err);
      }
    });

    this.servicoService.getVeterinarios().subscribe({
      next: (data) => { this.veterinarios = data; },
      error: (err) => {
        this.errorMessage = 'Não foi possível carregar a lista de veterinários.';
        console.error(err);
      }
    });
  }

  onSubmit(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.servicoService.createServico(this.novoServico).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = `Serviço "${response.tipo}" cadastrado com sucesso!`;
        this.resetForm();
        this.cdr.detectChanges();
        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 4000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Ocorreu um erro ao cadastrar o serviço.';
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }

  resetForm(): void {
    this.novoServico = { tipo: '', veterinarioId: 0, valor: 0 };
  }
}
