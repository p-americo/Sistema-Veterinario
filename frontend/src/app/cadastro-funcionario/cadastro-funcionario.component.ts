// PASSO 1: Importe o ChangeDetectorRef
import { Component, EventEmitter, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FuncionarioService } from '../services/funcionario.service';
import { Cargo, FuncionarioRequest } from '../models/funcionario.model';

@Component({
  selector: 'app-cadastro-funcionario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro-funcionario.component.html',
  styleUrls: ['./cadastro-funcionario.component.css']
})
export class CadastroFuncionarioComponent implements OnInit {
  @Output() voltar = new EventEmitter<void>();

  novoFuncionario: FuncionarioRequest = {
    nome: '', cpf: '', dataNascimento: '', telefone: '', email: '',
    dataAdmissao: '', cargoId: 0, crmv: ''
  };

  cargos: Cargo[] = [];
  successMessage: string | null = null;
  errorMessage: string | null = null;
  isLoading = false;


  constructor(
    private funcionarioService: FuncionarioService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCargos();
  }

  loadCargos(): void {
    this.funcionarioService.getCargos().subscribe({
      next: (data) => { this.cargos = data; },
      error: (err) => {
        this.errorMessage = 'Não foi possível carregar os cargos.';
        console.error('Erro ao buscar cargos!', err);
      }
    });
  }

  onSubmit(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const request = { ...this.novoFuncionario };
    if (!request.crmv) {
      delete request.crmv;
    }

    this.funcionarioService.createFuncionario(request).subscribe({
      next: (response) => {

        this.successMessage = `Funcionário ${response.nome} cadastrado com sucesso!`;
        this.isLoading = false;


        this.cdr.detectChanges();


        this.resetForm();
        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 4000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Ocorreu um erro ao cadastrar. Verifique os dados.';
        console.error('Erro ao cadastrar funcionário!', err);


        this.cdr.detectChanges();
      }
    });
  }

  resetForm(): void {
    this.novoFuncionario = {
      nome: '', cpf: '', dataNascimento: '', telefone: '', email: '',
      dataAdmissao: '', cargoId: 0, crmv: ''
    };
  }
}
