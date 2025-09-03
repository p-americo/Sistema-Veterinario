import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AgendamentoService } from '../../services/agendamento.service';
import { AgendamentoResponse } from '../../models/agendamento.model';
import { ClienteResponse } from '../../models/cliente.model';
import { AnimalResponse } from '../../models/animal.model';
import { ServicoResponse } from '../../models/servico.model';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-gestao-agendamento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-agendamento.html',
})
export class GestaoAgendamento implements OnInit {
  agendamentos: AgendamentoResponse[] = [];
  agendamentoForm: FormGroup;
  isEditing = false;
  currentAgendamentoId: number | null = null;
  errorMessage = '';
  successMessage = '';

  // Listas para os dropdowns
  listaClientes: ClienteResponse[] = [];
  listaAnimais: AnimalResponse[] = []; // A lista mestra com todos os animais
  listaAnimaisFiltrada: AnimalResponse[] = []; // A lista que será exibida no dropdown
  listaServicos: ServicoResponse[] = [];
  listaStatus: string[] = [];

  constructor(private fb: FormBuilder, private agendamentoService: AgendamentoService) {
    this.agendamentoForm = this.fb.group({
      clienteId: [null, Validators.required],
      animalId: [{ value: null, disabled: true }, Validators.required],
      servicoId: [null, Validators.required],
      dataHoraAgendamento: ['', Validators.required],
      status: ['AGENDADO', Validators.required],
      observacoes: [''],
    });
  }

  ngOnInit(): void {
    this.loadAgendamentos();
    this.loadDropdownData();
    this.onClienteChange();
  }

  // MODIFICAÇÃO 1: Lógica de filtro correta e limpa
  onClienteChange(): void {
    this.agendamentoForm.get('clienteId')?.valueChanges.subscribe(clienteIdSelecionado => {
      this.agendamentoForm.get('animalId')?.reset();
      this.agendamentoForm.get('animalId')?.disable();
      this.listaAnimaisFiltrada = [];

      if (clienteIdSelecionado) {
        // Filtra a lista mestra de animais, comparando o ID do cliente aninhado
        this.listaAnimaisFiltrada = this.listaAnimais.filter(animal =>
          animal.cliente && animal.cliente.id == clienteIdSelecionado
        );

        // Habilita o campo de animal apenas se houver animais para o cliente selecionado
        if (this.listaAnimaisFiltrada.length > 0) {
          this.agendamentoForm.get('animalId')?.enable();
        }
      }
    });
  }

  // MODIFICAÇÃO 2: Código de carregamento limpo, sem console.log
  loadDropdownData(): void {
    this.agendamentoService.getClientes().subscribe(data => this.listaClientes = data);
    this.agendamentoService.getAnimais().subscribe(data => this.listaAnimais = data);
    this.agendamentoService.getServicos().subscribe(data => this.listaServicos = data);
    this.agendamentoService.getAgendamentoStatus().subscribe(data => this.listaStatus = data);
  }

  loadAgendamentos(): void {
    this.agendamentoService.getAgendamentos().subscribe(data => this.agendamentos = data);
  }

  onSubmit(): void {
    if (this.agendamentoForm.invalid) {
      this.errorMessage = "Por favor, preencha todos os campos obrigatórios.";
      return;
    }
    const data = this.agendamentoForm.getRawValue(); // Usa getRawValue para incluir campos desabilitados se necessário
    const agendamentoData = { ...data, dataHoraAgendamento: new Date(data.dataHoraAgendamento).toISOString() };

    const operation = this.isEditing && this.currentAgendamentoId
      ? this.agendamentoService.updateAgendamento(this.currentAgendamentoId, agendamentoData)
      : this.agendamentoService.createAgendamento(agendamentoData);

    operation.subscribe({
      next: () => {
        this.successMessage = `Agendamento ${this.isEditing ? 'atualizado' : 'criado'} com sucesso!`;
        this.resetForm();
      },
      error: (err) => this.errorMessage = `Erro ao ${this.isEditing ? 'atualizar' : 'criar'} agendamento.`
    });
  }

  editAgendamento(agendamento: AgendamentoResponse): void {
    this.isEditing = true;
    this.currentAgendamentoId = agendamento.id;

    // Dispara o filtro de animais ANTES de preencher o formulário
    this.agendamentoForm.get('clienteId')?.setValue(agendamento.cliente.id);

    // Formata a data para o input datetime-local, evitando problemas de fuso horário
    const date = new Date(agendamento.dataHoraAgendamento);
    const timezoneOffset = date.getTimezoneOffset() * 60000;
    const localDateTime = new Date(date.getTime() - timezoneOffset).toISOString().slice(0, 16);

    this.agendamentoForm.patchValue({
      animalId: agendamento.animal.id,
      servicoId: agendamento.servico.id,
      dataHoraAgendamento: localDateTime,
      status: agendamento.status,
      observacoes: agendamento.observacoes,
    });
  }

  deleteAgendamento(id: number): void {
    if (confirm('Tem certeza que deseja excluir este agendamento?')) {
      this.agendamentoService.deleteAgendamento(id).subscribe({
        next: () => {
          this.successMessage = 'Agendamento excluído com sucesso!';
          this.loadAgendamentos();
        },
        error: (err) => this.errorMessage = 'Erro ao excluir agendamento.'
      });
    }
  }

  resetForm(): void {
    this.isEditing = false;
    this.currentAgendamentoId = null;
    this.agendamentoForm.reset({
      status: 'AGENDADO',
      animalId: { value: null, disabled: true } // MODIFICAÇÃO 3: Garante que o campo animal resete para desabilitado
    });
    this.listaAnimaisFiltrada = [];
    this.loadAgendamentos();
  }
}
