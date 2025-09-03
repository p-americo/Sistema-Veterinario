import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ClienteService } from '../../../services/cliente.service';
import { ClienteRequest, ClienteResponse, ClienteUpdate } from '../../../models/cliente.model';

@Component({
  selector: 'app-gestao-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-cliente.html',
  //styleUrl: './gestao-cliente.css'
})
export class GestaoCliente implements OnInit {
  clientes: ClienteResponse[] = [];
  clienteForm: FormGroup;
  searchForm: FormGroup;
  isEditing = false;
  currentClienteId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private clienteService: ClienteService,
    private fb: FormBuilder
  ) {
    this.clienteForm = this.fb.group({
      nome: ['', [Validators.required, Validators.maxLength(100)]],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      dataNascimento: ['', Validators.required],
      telefone: ['', [Validators.required, Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]]
    });

    this.searchForm = this.fb.group({
      searchTerm: [''],
      searchType: ['nome'] // 'nome' or 'id'
    });
  }

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.clienteService.getClientes().subscribe({
      next: (data) => {
        this.clientes = data;
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar clientes: ' + error.message;
      }
    });
  }

  searchClientes(): void {
    const searchTerm = this.searchForm.get('searchTerm')?.value;
    const searchType = this.searchForm.get('searchType')?.value;

    if (!searchTerm) {
      this.loadClientes();
      return;
    }

    if (searchType === 'nome') {
      this.clienteService.searchClientesByName(searchTerm).subscribe({
        next: (data) => {
          this.clientes = data;
        },
        error: (error) => {
          this.errorMessage = 'Erro ao buscar clientes: ' + error.message;
        }
      });
    } else if (searchType === 'id') {
      const id = parseInt(searchTerm, 10);
      if (isNaN(id)) {
        this.errorMessage = 'ID inválido';
        return;
      }

      this.clienteService.getClienteById(id).subscribe({
        next: (data) => {
          this.clientes = [data];
        },
        error: (error) => {
          this.errorMessage = 'Erro ao buscar cliente: ' + error.message;
          this.clientes = [];
        }
      });
    }
  }

  onSubmit(): void {
    if (this.clienteForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos corretamente.';
      return;
    }

    const cliente = this.clienteForm.value;

    if (this.isEditing && this.currentClienteId) {
      this.clienteService.updateCliente(this.currentClienteId, cliente).subscribe({
        next: () => {
          this.successMessage = 'Cliente atualizado com sucesso!';
          this.resetForm();
          this.loadClientes();
        },
        error: (error) => {
          this.errorMessage = 'Erro ao atualizar cliente: ' + error.message;
        }
      });
    } else {
      this.clienteService.createCliente(cliente).subscribe({
        next: () => {
          this.successMessage = 'Cliente criado com sucesso!';
          this.resetForm();
          this.loadClientes();
        },
        error: (error) => {
          this.errorMessage = 'Erro ao criar cliente: ' + error.message;
        }
      });
    }
  }

  editCliente(cliente: ClienteResponse): void {
    this.isEditing = true;
    this.currentClienteId = cliente.id;

    // Format date from ISO string to YYYY-MM-DD for input[type=date]
    const dataNascimento = cliente.dataNascimento.split('T')[0];

    this.clienteForm.patchValue({
      nome: cliente.nome,
      cpf: cliente.cpf,
      dataNascimento: dataNascimento,
      telefone: cliente.telefone,
      email: cliente.email
    });
  }

  deleteCliente(id: number): void {
    if (confirm('Tem certeza que deseja excluir este cliente?')) {
      this.clienteService.deleteCliente(id).subscribe({
        next: () => {
          this.successMessage = 'Cliente excluído com sucesso!';
          this.loadClientes();
        },
        error: (error) => {
          this.errorMessage = 'Erro ao excluir cliente: ' + error.message;
        }
      });
    }
  }

  resetForm(): void {
    this.clienteForm.reset();
    this.isEditing = false;
    this.currentClienteId = null;
    this.errorMessage = '';
    this.successMessage = '';
  }
}
