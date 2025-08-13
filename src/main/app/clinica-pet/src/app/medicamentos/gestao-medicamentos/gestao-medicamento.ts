import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MedicamentoService } from '../../services/medicamento.service';
import { MedicamentoResponse } from '../../models/medicamento.model';

@Component({
  selector: 'app-gestao-medicamento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-medicamento.html',
})
export class GestaoMedicamento implements OnInit {
  medicamentos: MedicamentoResponse[] = [];
  medicamentoForm: FormGroup;
  isEditing = false;
  currentMedicamentoId: number | null = null;
  errorMessage = '';
  successMessage = '';

  listaCategorias: string[] = [];
  listaVias: string[] = [];

  constructor(private fb: FormBuilder, private medicamentoService: MedicamentoService) {
    this.medicamentoForm = this.fb.group({
      nome: ['', Validators.required],
      descricao: [''],
      quantidadeEstoque: [0, [Validators.required, Validators.min(0)]],
      categoria: [null, Validators.required],
      viaAdministracao: [null],
      dosagemPadrao: [''],
      principioAtivo: [''],
      fabricante: [''],
      prescricaoObrigatoria: [false, Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadMedicamentos();
    this.loadDropdownData();
  }

  loadDropdownData(): void {
    this.medicamentoService.getCategorias().subscribe(data => this.listaCategorias = data);
    this.medicamentoService.getViasAdministracao().subscribe(data => this.listaVias = data);
  }

  loadMedicamentos(): void {
    this.medicamentoService.getMedicamentos().subscribe(data => this.medicamentos = data);
  }

  onSubmit(): void {
    if (this.medicamentoForm.invalid) {
      this.errorMessage = 'Formulário inválido. Verifique os campos obrigatórios.';
      return;
    }

    const medicamentoData = this.medicamentoForm.value;
    const operation = this.isEditing && this.currentMedicamentoId
      ? this.medicamentoService.updateMedicamento(this.currentMedicamentoId, medicamentoData)
      : this.medicamentoService.createMedicamento(medicamentoData);

    operation.subscribe({
      next: () => {
        this.successMessage = `Medicamento ${this.isEditing ? 'atualizado' : 'cadastrado'} com sucesso!`;
        this.resetForm();
      },
      error: (err) => this.errorMessage = 'Erro ao salvar medicamento.'
    });
  }

  editMedicamento(medicamento: MedicamentoResponse): void {
    this.isEditing = true;
    this.currentMedicamentoId = medicamento.id;
    this.medicamentoForm.patchValue(medicamento);
  }

  deleteMedicamento(id: number): void {
    if (confirm('Tem certeza que deseja excluir este medicamento?')) {
      this.medicamentoService.deleteMedicamento(id).subscribe(() => {
        this.successMessage = 'Medicamento excluído com sucesso!';
        this.loadMedicamentos();
      });
    }
  }

  resetForm(): void {
    this.medicamentoForm.reset({ quantidadeEstoque: 0, prescricaoObrigatoria: false });
    this.isEditing = false;
    this.currentMedicamentoId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.loadMedicamentos();
  }
}
