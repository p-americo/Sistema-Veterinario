// src/app/servico/gestao-servico/gestao-servico.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ServicoService } from '../../services/servico.service';
import { ServicoResponse } from '../../models/servico.model';
import { FuncionarioResponse } from '../../models/funcionario.model';

@Component({
  selector: 'app-gestao-servico',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-servico.html',
})
export class GestaoServico implements OnInit {
  servicos: ServicoResponse[] = [];
  servicoForm: FormGroup;
  isEditing = false;
  currentServicoId: number | null = null;
  errorMessage = '';
  successMessage = '';

  listaTipos: string[] = [];
  listaVeterinarios: FuncionarioResponse[] = [];

  constructor(private fb: FormBuilder, private servicoService: ServicoService) {
    this.servicoForm = this.fb.group({
      tipo: [null, Validators.required],
      veterinarioId: [null, Validators.required],
      valor: [null, [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    this.loadServicos();
    this.loadDropdownData();
  }

  loadDropdownData(): void {
    this.servicoService.getServicoTipos().subscribe(data => this.listaTipos = data);
    this.servicoService.getVeterinarios().subscribe(data => this.listaVeterinarios = data);
  }

  loadServicos(): void {
    this.servicoService.getServicos().subscribe(data => this.servicos = data);
  }

  onSubmit(): void {
    if (this.servicoForm.invalid) { return; }
    const servicoData = this.servicoForm.value;

    const operation = this.isEditing && this.currentServicoId
      ? this.servicoService.updateServico(this.currentServicoId, servicoData)
      : this.servicoService.createServico(servicoData);

    operation.subscribe({
      next: () => {
        this.successMessage = `Serviço ${this.isEditing ? 'atualizado' : 'cadastrado'} com sucesso!`;
        this.resetForm();
      },
      error: (err) => this.errorMessage = 'Erro ao salvar serviço.'
    });
  }

  editServico(servico: ServicoResponse): void {
    this.isEditing = true;
    this.currentServicoId = servico.id;
    this.servicoForm.patchValue({
      tipo: servico.tipo,
      veterinarioId: servico.veterinarioId, // <-- A CORREÇÃO PRINCIPAL
      valor: servico.valor
    });
  }

  deleteServico(id: number): void {
    if (confirm('Tem certeza que deseja excluir este serviço?')) {
      this.servicoService.deleteServico(id).subscribe({
        next: () => {
          this.successMessage = 'Serviço excluído com sucesso!';
          this.loadServicos();
        },
        error: (err) => this.errorMessage = 'Erro ao excluir serviço.'
      });
    }
  }

  resetForm(): void {
    this.servicoForm.reset();
    this.isEditing = false;
    this.currentServicoId = null;
    this.loadServicos();
  }
}
