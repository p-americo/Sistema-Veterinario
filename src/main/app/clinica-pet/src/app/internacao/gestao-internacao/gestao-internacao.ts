import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InternacaoService } from '../../services/internacao.service';
import { InternacaoResponse, DiariaResponse } from '../../models/internacao.model';
import { AnimalResponse } from '../../models/animal.model';
import { MedicamentoResponse } from '../../models/medicamento.model';
import { FuncionarioResponse } from '../../models/funcionario.model';

@Component({
  selector: 'app-gestao-internacao',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-internacao.html',
})
export class GestaoInternacao implements OnInit {
  animalSelectControl = new FormControl<number | null>(null);
  internacaoForm: FormGroup;
  diariaForm: FormGroup;
  medicamentoForm: FormGroup;

  listaAnimais: AnimalResponse[] = [];
  listaMedicamentos: MedicamentoResponse[] = [];
  listaFuncionarios: FuncionarioResponse[] = [];

  animalSelecionado: AnimalResponse | null = null;
  internacaoAtiva: InternacaoResponse | null = null;
  diariaSelecionada: DiariaResponse | null = null;

  statusBusca: 'inicial' | 'buscando' | 'encontrado' | 'nao_encontrado' = 'inicial';

  showDiariaModal = false;
  showMedicamentoModal = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private internacaoService: InternacaoService,
    private cdr: ChangeDetectorRef // Injetando o ChangeDetectorRef
  ) {
    this.internacaoForm = this.fb.group({
      dataEntrada: ['', Validators.required]
    });
    this.diariaForm = this.fb.group({
      dataHora: ['', Validators.required],
      pesoNoDia: [''],
      observacoesClinicas: ['', Validators.required],
      diagnostico: ['']
    });
    this.medicamentoForm = this.fb.group({
      medicamentoId: [null, Validators.required],
      funcionarioExecutorId: [null, Validators.required],
      dataHora: ['', Validators.required],
      dosagem: ['', Validators.required],
      quantidadeAdministrada: [1, [Validators.required, Validators.min(0.1)]]
    });
  }

  ngOnInit(): void {
    this.loadDropdownData();
    this.animalSelectControl.valueChanges.subscribe(() => {
      this.statusBusca = 'inicial';
      this.internacaoAtiva = null;
      this.animalSelecionado = null;
    });
  }

  loadDropdownData(): void {
    this.internacaoService.getAnimais().subscribe(data => this.listaAnimais = data);
    this.internacaoService.getMedicamentos().subscribe(data => this.listaMedicamentos = data);
    this.internacaoService.getFuncionarios().subscribe(data => this.listaFuncionarios = data);
  }

  buscarInternacao(): void {
    const animalId = this.animalSelectControl.value;
    if (!animalId) return;

    this.animalSelecionado = this.listaAnimais.find(a => a.id == animalId) || null;

    if (!this.animalSelecionado) {
      this.setErrorMessage("Animal não encontrado na lista local.");
      return;
    }

    this.statusBusca = 'buscando';
    this.internacaoAtiva = null;
    this.clearMessages();

    this.internacaoService.getInternacaoAtivaPorAnimalId(animalId).subscribe({
      next: (data) => {
        this.internacaoAtiva = data;
        this.statusBusca = 'encontrado';
      },
      error: (err) => {
        // Trata qualquer erro como "não encontrado" para mostrar o formulário
        this.statusBusca = 'nao_encontrado';
        this.internacaoForm.patchValue({ dataEntrada: new Date().toLocaleString('sv-SE').replace(' ', 'T').slice(0, 16) });
        // Força o Angular a redesenhar a tela
        this.cdr.detectChanges();
      }
    });
  }

  iniciarInternacao(): void {
    if (!this.animalSelecionado || this.internacaoForm.invalid) {
      this.setErrorMessage("Formulário inválido ou animal não selecionado.");
      return;
    }

    const request = {
      animalId: this.animalSelecionado.id,
      dataEntrada: new Date(this.internacaoForm.value.dataEntrada).toISOString()
    };

    this.internacaoService.iniciarInternacao(request).subscribe({
      next: (data) => {
        this.internacaoAtiva = data;
        this.statusBusca = 'encontrado';
        this.setSuccessMessage("Internação iniciada com sucesso!");
      },
      error: (err) => this.setErrorMessage("Falha ao iniciar internação: " + (err.error?.message || err.message))
    });
  }

  darAlta(): void {
    if (!this.internacaoAtiva) return;
    if (confirm('Tem certeza que deseja dar alta para este animal?')) {
      this.internacaoService.darAltaInternacao(this.internacaoAtiva.id).subscribe({
        next: () => {
          this.internacaoAtiva = null;
          this.statusBusca = 'inicial';
          this.animalSelectControl.reset();
          this.setSuccessMessage("Alta registrada com sucesso!");
        },
        error: (err) => this.setErrorMessage("Falha ao registrar a alta.")
      });
    }
  }

  onDiariaSubmit(): void {
    if (!this.internacaoAtiva) return;
    const request = {
      internacaoId: this.internacaoAtiva.id,
      ...this.diariaForm.value,
      dataHora: new Date(this.diariaForm.value.dataHora).toISOString()
    };
    this.internacaoService.createDiaria(request).subscribe({
      next: (data) => {
        this.internacaoAtiva = data;
        this.closeDiariaModal();
        this.setSuccessMessage("Diária registrada com sucesso!");
      },
      error: (err) => this.setErrorMessage("Falha ao registrar a diária.")
    });
  }

  onMedicamentoSubmit(): void {
    if (!this.diariaSelecionada) return;
    const request = {
      diariaId: this.diariaSelecionada.id,
      ...this.medicamentoForm.value,
      dataHora: new Date(this.medicamentoForm.value.dataHora).toISOString()
    };
    this.internacaoService.createAdministracaoMedicamento(request).subscribe({
      next: (data) => {
        this.internacaoAtiva = data;
        this.closeMedicamentoModal();
        this.setSuccessMessage("Medicamento administrado com sucesso!");
      },
      error: (err) => this.setErrorMessage("Falha ao administrar medicamento.")
    });
  }

  private setSuccessMessage(message: string): void {
    this.clearMessages();
    this.successMessage = message;
    setTimeout(() => this.successMessage = '', 5000);
  }

  private setErrorMessage(message: string): void {
    this.clearMessages();
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  openDiariaModal(): void {
    this.diariaForm.reset();
    this.diariaForm.patchValue({ dataHora: new Date().toLocaleString('sv-SE').replace(' ', 'T').slice(0, 16) });
    this.showDiariaModal = true;
  }
  closeDiariaModal(): void { this.showDiariaModal = false; }

  openMedicamentoModal(diaria: DiariaResponse): void {
    this.diariaSelecionada = diaria;
    this.medicamentoForm.reset({ quantidadeAdministrada: 1 });
    this.medicamentoForm.patchValue({ dataHora: new Date().toLocaleString('sv-SE').replace(' ', 'T').slice(0, 16) });
    this.showMedicamentoModal = true;
  }
  closeMedicamentoModal(): void { this.showMedicamentoModal = false; this.diariaSelecionada = null; }
}
