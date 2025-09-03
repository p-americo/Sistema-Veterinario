// src/app/pessoa/Funcionario/gestao-funcionario/gestao-funcionario.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { FuncionarioService } from '../../../services/funcionario.service';
import { FuncionarioResponse, Cargo } from '../../../models/funcionario.model';

@Component({
  selector: 'app-gestao-funcionario',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-funcionario.html',
  //styleUrl: './gestao-funcionario.css'
})
export class GestaoFuncionario implements OnInit {
  funcionarios: FuncionarioResponse[] = [];
  // TODO: Criar e carregar a lista de cargos para o dropdown
  listaCargos: Cargo[] = [];
  funcionarioForm: FormGroup;
  searchForm: FormGroup;
  isEditing = false;
  currentFuncionarioId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private funcionarioService: FuncionarioService,
    private fb: FormBuilder
  ) {
    this.funcionarioForm = this.fb.group({
      // Campos de Pessoa
      nome: ['', [Validators.required, Validators.maxLength(100)]],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      dataNascimento: ['', Validators.required],
      telefone: ['', [Validators.required, Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      // Campos de Funcionario
      dataAdmissao: ['', Validators.required],
      cargoId: [null, Validators.required],
      crmv: ['']
    });

    this.searchForm = this.fb.group({
      searchTerm: [''],
      searchType: ['nome']
    });
  }

  ngOnInit(): void {
    this.loadFuncionarios();
    this.loadCargos();

  }

  loadCargos(): void {
    this.funcionarioService.getCargos().subscribe({
      next: (data) => {
        this.listaCargos = data; // A 'data' agora é a lista de objetos
        console.log('CARGOS RECEBIDOS:', data); // <-- ADICIONE ESTA LINHA
        console.log('CARGOS RECEBIDOS:', data); // <-- ADICIONE ESTA LINHA
      },
      error: (err) => this.errorMessage = 'Erro ao carregar a lista de cargos.'
    });
  }

  loadFuncionarios(): void {
    this.funcionarioService.getFuncionarios().subscribe({
      next: (data) => this.funcionarios = data,
      error: (err) => this.errorMessage = 'Erro ao carregar funcionários: ' + err.message
    });
  }

  searchFuncionarios(): void {
    const searchTerm = this.searchForm.get('searchTerm')?.value;
    const searchType = this.searchForm.get('searchType')?.value;

    if (!searchTerm) {
      this.loadFuncionarios();
      return;
    }

    if (searchType === 'nome') {
      this.funcionarioService.searchFuncionariosByName(searchTerm).subscribe({
        next: (data) => this.funcionarios = data,
        error: (err) => this.errorMessage = 'Erro ao buscar funcionários: ' + err.message
      });
    } else if (searchType === 'id') {
      const id = parseInt(searchTerm, 10);
      if (isNaN(id)) {
        this.errorMessage = 'ID inválido';
        return;
      }
      this.funcionarioService.getFuncionarioById(id).subscribe({
        next: (data) => this.funcionarios = [data],
        error: (err) => {
          this.errorMessage = 'Erro ao buscar funcionário: ' + err.message;
          this.funcionarios = [];
        }
      });
    }
  }

  onSubmit(): void {
    if (this.funcionarioForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios corretamente.';
      return;
    }

    const funcionarioData = this.funcionarioForm.value;

    if (this.isEditing && this.currentFuncionarioId) {
      this.funcionarioService.updateFuncionario(this.currentFuncionarioId, funcionarioData).subscribe({
        next: () => {
          this.successMessage = 'Funcionário atualizado com sucesso!';
          this.resetForm();
          this.loadFuncionarios();
        },
        error: (err) => this.errorMessage = 'Erro ao atualizar funcionário: ' + err.message
      });
    } else {
      this.funcionarioService.createFuncionario(funcionarioData).subscribe({
        next: () => {
          this.successMessage = 'Funcionário criado com sucesso!';
          this.resetForm();
          this.loadFuncionarios();
        },
        error: (err) => this.errorMessage = 'Erro ao criar funcionário: ' + err.message
      });
    }
  }

  editFuncionario(func: FuncionarioResponse): void {
    this.isEditing = true;
    this.currentFuncionarioId = func.id;

    const dataNascimento = func.dataNascimento.split('T')[0];
    const dataAdmissao = func.dataAdmissao.split('T')[0];

    this.funcionarioForm.patchValue({
      nome: func.nome,
      cpf: func.cpf,
      dataNascimento: dataNascimento,
      telefone: func.telefone,
      email: func.email,
      dataAdmissao: dataAdmissao,
      cargoId: func.cargo.id,
      crmv: func.crmv
    });
  }

  deleteFuncionario(id: number): void {
    if (confirm('Tem certeza que deseja excluir este funcionário?')) {
      this.funcionarioService.deleteFuncionario(id).subscribe({
        next: () => {
          this.successMessage = 'Funcionário excluído com sucesso!';
          this.loadFuncionarios();
        },
        error: (err) => this.errorMessage = 'Erro ao excluir funcionário: ' + err.message
      });
    }
  }

  resetForm(): void {
    this.funcionarioForm.reset({ cargoId: null });
    this.isEditing = false;
    this.currentFuncionarioId = null;
    this.errorMessage = '';
    this.successMessage = '';
  }
}
