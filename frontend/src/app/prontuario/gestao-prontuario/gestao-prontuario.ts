import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ProntuarioService } from '../../services/prontuario.service';
import { ProntuarioResponse, RegistroProntuarioResponse } from '../../models/prontuario.model';
import { AnimalResponse } from '../../models/animal.model';
import { FuncionarioResponse } from '../../models/funcionario.model';

@Component({
  selector: 'app-gestao-prontuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-prontuario.html',
})
export class GestaoProntuario implements OnInit {
  prontuarioForm: FormGroup;
  registroForm: FormGroup;
  animalSelectControl = new FormControl<number | null>(null);

  listaAnimais: AnimalResponse[] = [];
  listaVeterinarios: FuncionarioResponse[] = [];
  prontuarioAtual: ProntuarioResponse | null = null;

  showRegistroModal = false;
  isEditingRegistro = false;
  currentRegistroId: number | null = null;

  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private prontuarioService: ProntuarioService) {
    this.prontuarioForm = this.fb.group({
      alergiasConhecidas: [''],
      condicoesPreexistentes: ['']
    });

    this.registroForm = this.fb.group({
      veterinarioResponsavelId: [null, Validators.required],
      dataHora: ['', Validators.required],
      pesoNoDia: [null],
      observacoesClinicas: ['', Validators.required],
      diagnostico: ['']
    });
  }

  ngOnInit(): void {
    this.loadInitialData();
    this.onAnimalSelectChange();
  }

  loadInitialData(): void {
    this.prontuarioService.getAnimais().subscribe(data => this.listaAnimais = data);
    this.prontuarioService.getVeterinarios().subscribe(data => this.listaVeterinarios = data);
  }

  onAnimalSelectChange(): void {
    this.animalSelectControl.valueChanges.subscribe(animalId => {
      this.prontuarioAtual = null;
      this.prontuarioForm.reset();
      this.closeRegistroModal();
      if (animalId) {
        this.loadProntuario(animalId);
      }
    });
  }

  loadProntuario(animalId: number): void {
    this.prontuarioService.getProntuarioByAnimalId(animalId).subscribe({
      next: (data) => {
        this.prontuarioAtual = data;
        this.prontuarioForm.patchValue({
          alergiasConhecidas: data.alergiasConhecidas,
          condicoesPreexistentes: data.condicoesPreexistentes
        });
      },
      error: (err) => {
        if (err.status === 404) {
          const animalSelecionado = this.listaAnimais.find(a => a.id === animalId);
          this.prontuarioAtual = {
            id: 0,
            animalId: animalId,
            nomeAnimal: animalSelecionado ? animalSelecionado.nome : 'Desconhecido',
            registros: []
          };
        } else {
          this.errorMessage = 'Erro ao buscar prontuário.';
        }
      }
    });
  }

  // --- LÓGICA DE SALVAMENTO CORRIGIDA ---

  // Este método salva/cria as informações GERAIS do prontuário (alergias, etc)
  onProntuarioSubmit(): void {
    if (!this.prontuarioAtual || !this.animalSelectControl.value) { return; }

    const prontuarioData = {
      animalId: this.animalSelectControl.value,
      ...this.prontuarioForm.value
    };

    // Se o ID é 0, significa que precisamos CRIAR o prontuário primeiro
    const operation = this.prontuarioAtual.id
      ? this.prontuarioService.updateProntuario(this.prontuarioAtual.id, prontuarioData)
      : this.prontuarioService.createProntuario(prontuarioData);

    operation.subscribe({
      next: (data) => {
        this.successMessage = `Prontuário salvo com sucesso!`;
        // ATUALIZA O PRONTUÁRIO ATUAL COM O ID REAL VINDO DO BACKEND
        this.prontuarioAtual = data;
      },
      error: (err) => this.errorMessage = 'Erro ao salvar prontuário.'
    });
  }

  // Este método salva um NOVO REGISTRO no prontuário que JÁ EXISTE
  onRegistroSubmit(): void {
    if (this.registroForm.invalid || !this.prontuarioAtual || this.prontuarioAtual.id === 0) {
      this.errorMessage = "É necessário criar o prontuário principal antes de adicionar registros.";
      return;
    }

    const registroData = {
      prontuarioId: this.prontuarioAtual.id, // Agora usa o ID real
      ...this.registroForm.value,
      dataHora: new Date(this.registroForm.value.dataHora).toISOString()
    };

    // A lógica de criar/atualizar um registro é separada
    const operation = this.isEditingRegistro && this.currentRegistroId
      ? this.prontuarioService.updateRegistroProntuario(this.currentRegistroId, registroData)
      : this.prontuarioService.createRegistroProntuario(registroData);

    operation.subscribe({
      next: (updatedProntuario) => {
        this.successMessage = `Registro salvo com sucesso!`;
        this.prontuarioAtual = updatedProntuario;
        this.closeRegistroModal();
      },
      error: (err) => this.errorMessage = 'Erro ao salvar o registro.'
    });
  }

  // --- MÉTODOS DO MODAL (com pequenas alterações) ---

  openNewRegistroModal(): void {
    this.isEditingRegistro = false;
    this.registroForm.reset();
    // Preenche a data e hora atuais como sugestão
    this.registroForm.patchValue({
      dataHora: new Date().toLocaleString('sv-SE').replace(' ', 'T').slice(0, 16)
    });
    this.showRegistroModal = true;
  }

  openEditRegistroModal(registro: RegistroProntuarioResponse): void {
    this.isEditingRegistro = true;
    this.currentRegistroId = registro.id;
    const localDateTime = new Date(registro.dataHora).toLocaleString('sv-SE').replace(' ', 'T').slice(0, 16);

    this.registroForm.patchValue({
      veterinarioResponsavelId: registro.veterinarioResponsavelId,
      dataHora: localDateTime,
      pesoNoDia: registro.pesoNoDia,
      observacoesClinicas: registro.observacoesClinicas,
      diagnostico: registro.diagnostico
    });
    this.showRegistroModal = true;
  }

  closeRegistroModal(): void {
    this.showRegistroModal = false;
    this.currentRegistroId = null;
  }

  deleteRegistro(registroId: number): void {
    if (confirm('Tem certeza que deseja excluir este registro?')) {
      this.prontuarioService.deleteRegistroProntuario(registroId).subscribe({
        next: () => {
          this.successMessage = 'Registro excluído com sucesso!';
          if (this.animalSelectControl.value) {
            this.loadProntuario(this.animalSelectControl.value); // Recarrega o prontuário
          }
        },
        error: (err) => this.errorMessage = 'Erro ao excluir o registro.'
      });
    }
  }
}
