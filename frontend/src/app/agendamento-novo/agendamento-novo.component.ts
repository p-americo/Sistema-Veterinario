import { Component, Input, OnInit, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnimalResponse } from '../models/animal.model';
import { AgendamentoService } from '../services/agendamento.service';
import { ServicoResponse } from '../models/servico.model';
import { AgendamentoRequest } from '../models/agendamento.model';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

interface CalendarioDia {
  data: Date;
  outroMes: boolean;
}

@Component({
  selector: 'app-agendamento-novo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [DatePipe],
  templateUrl: './agendamento-novo.component.html',
  styleUrls: ['./agendamento-novo.component.css']
})
export class AgendamentoNovoComponent implements OnInit {

  @Input() clienteId!: number;
  @Input() petsDoCliente: AnimalResponse[] = [];
  @Output() agendamentoConcluido = new EventEmitter<void>();
  @Output() formularioFechado = new EventEmitter<void>();

  petSelecionado: AnimalResponse | null = null;
  servicos: ServicoResponse[] = [];
  servicoSelecionadoId: number | null = null;
  imagemPetUrl: SafeUrl | null = null;

  mesAtual: Date = new Date();
  diaSelecionado: Date | null = null;
  diasDaSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  diasDoMes: CalendarioDia[] = [];
  hoje: Date = new Date();

  horariosDisponiveis = [
    '08:30', '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
    '13:00', '13:30', '14:00', '14:30', '15:00', '15:30', '16:00'
  ];
  horarioSelecionado: string | null = null;

  mensagemSucesso: string | null = null;
  mensagemErro: string | null = null;
  isLoading = false;

  constructor(
    private agendamentoService: AgendamentoService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {
    this.hoje.setHours(0, 0, 0, 0);
  }

  ngOnInit(): void {
    this.gerarDiasDoMes();
    this.loadServicos();
  }

  onPetSelecionado(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const petId = Number(selectElement.value);
    this.petSelecionado = this.petsDoCliente.find(p => p.id === petId) || null;

    if (this.petSelecionado && this.petSelecionado.id) {
      const url = `http://localhost:8080/api/animais/imagem/${this.petSelecionado.id}`;
      this.imagemPetUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    } else {
      this.imagemPetUrl = null;
    }

    this.cdr.detectChanges();
  }

  voltarParaMenu(): void {
    this.formularioFechado.emit();
  }

  confirmarAgendamento(): void {
    if (!this.isFormularioValido() || this.isLoading) {
      return;
    }
    this.isLoading = true;
    this.mensagemErro = null;
    this.mensagemSucesso = null;

    const [horas, minutos] = this.horarioSelecionado!.split(':');
    const dataAgendamento = new Date(this.diaSelecionado!);
    dataAgendamento.setHours(Number(horas), Number(minutos), 0, 0);

    const tzoffset = dataAgendamento.getTimezoneOffset() * 60000;
    const dataLocalISO = new Date(dataAgendamento.getTime() - tzoffset).toISOString().slice(0, 16);

    const agendamentoRequest: AgendamentoRequest = {
      clienteId: this.clienteId,
      animalId: this.petSelecionado!.id,
      servicoId: this.servicoSelecionadoId!,
      dataHoraAgendamento: dataLocalISO,
      status: 'AGENDADO'
    };

    this.agendamentoService.createAgendamento(agendamentoRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.mensagemSucesso = `Agendamento para ${this.petSelecionado?.nome} realizado com sucesso!`;
        setTimeout(() => {
          this.agendamentoConcluido.emit();
          this.resetForm();
        }, 3000);
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erro ao criar agendamento!', err);
        this.mensagemErro = err.error?.message || 'Ocorreu um erro ao agendar. Tente novamente.';
      }
    });
  }

  resetForm(): void {
    this.mensagemSucesso = null;
    this.mensagemErro = null;
    this.diaSelecionado = null;
    this.horarioSelecionado = null;
    this.servicoSelecionadoId = null;
    this.petSelecionado = null;
    this.imagemPetUrl = null;
  }

  isDiaPassado(data: Date): boolean { return data < this.hoje; }
  loadServicos(): void { this.agendamentoService.getServicos().subscribe({ next: (data) => { this.servicos = data; }, error: (err) => { console.error('Erro ao buscar serviços!', err); this.mensagemErro = 'Não foi possível carregar os serviços.'; } }); }
  isFormularioValido(): boolean { return !!this.petSelecionado && !!this.servicoSelecionadoId && !!this.diaSelecionado && !!this.horarioSelecionado; }
  gerarDiasDoMes(): void { this.diasDoMes = []; const primeiroDiaDoMes = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth(), 1); const ultimoDiaDoMes = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() + 1, 0); for (let i = primeiroDiaDoMes.getDay(); i > 0; i--) { const data = new Date(primeiroDiaDoMes); data.setDate(data.getDate() - i); this.diasDoMes.push({ data, outroMes: true }); } for (let i = 1; i <= ultimoDiaDoMes.getDate(); i++) { const data = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth(), i); this.diasDoMes.push({ data, outroMes: false }); } const ultimoDiaGrid = this.diasDoMes[this.diasDoMes.length - 1].data; const diasParaCompletar = 6 - ultimoDiaGrid.getDay(); for (let i = 1; i <= diasParaCompletar; i++) { const data = new Date(ultimoDiaGrid); data.setDate(data.getDate() + i); this.diasDoMes.push({ data, outroMes: true }); } }
  mesAnterior(): void { const novaData = new Date(this.mesAtual); novaData.setMonth(novaData.getMonth() - 1); this.mesAtual = novaData; this.gerarDiasDoMes(); }
  proximoMes(): void { const novaData = new Date(this.mesAtual); novaData.setMonth(novaData.getMonth() + 1); this.mesAtual = novaData; this.gerarDiasDoMes(); }
  selecionarDia(data: Date): void { this.diaSelecionado = data; }
  isDiaSelecionado(data: Date): boolean { if (!this.diaSelecionado) return false; return data.toDateString() === this.diaSelecionado.toDateString(); }
  selecionarHorario(horario: string): void { this.horarioSelecionado = horario; }
  calcularIdade(dataNascimento: string): number { if (!dataNascimento) return 0; const hoje = new Date(); const nascimento = new Date(dataNascimento); let idade = hoje.getFullYear() - nascimento.getFullYear(); const m = hoje.getMonth() - nascimento.getMonth(); if (m < 0 || (m === 0 && hoje.getDate() < nascimento.getDate())) { idade--; } return idade; }
}
