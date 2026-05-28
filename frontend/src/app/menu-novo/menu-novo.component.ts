// src/app/menu-novo/menu-novo.component.ts

import { Component, OnInit, ChangeDetectorRef, signal, computed, effect } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../services/cliente.service';
import { AnimalService } from '../services/animal.service';
import { AgendamentoService } from '../services/agendamento.service';
import { InternacaoService } from '../services/internacao.service';
import { SessionService } from '../services/session.service';
import { ClienteResponse } from '../models/cliente.model';
import { AnimalResponse } from '../models/animal.model';
import { AgendamentoResponse, AgendamentoRequest } from '../models/agendamento.model';
import { InternacaoResponse } from '../models/internacao.model';
import { ServicoResponse } from '../models/servico.model';
import { AnimalNovoComponent } from '../animal-novo/animal-novo.component';
import { AgendamentoNovoComponent } from '../agendamento-novo/agendamento-novo.component';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-menu-novo',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AnimalNovoComponent,
    AgendamentoNovoComponent
  ],
  providers: [DatePipe],
  templateUrl: './menu-novo.component.html',
  styleUrls: ['./menu-novo.component.css']
})
export class MenuNovoComponent implements OnInit {

  abaAtiva = signal<'agendamentos' | 'menu'>('menu');

  // Signals para Listas Auxiliares
  especiesSignal = signal<string[]>([]);
  portesSignal = signal<string[]>([]);
  sexosSignal = signal<string[]>([]);
  servicosSignal = signal<ServicoResponse[]>([]);

  // Signals de Estado do Formulário de Pet
  petFormIsLoading = signal<boolean>(false);
  petFormErrorMessage = signal<string | null>(null);
  petFormSuccessMessage = signal<string | null>(null);

  // Signals de Estado do Formulário de Agendamento
  agendamentoFormIsLoading = signal<boolean>(false);
  agendamentoFormErrorMessage = signal<string | null>(null);
  agendamentoFormSuccessMessage = signal<string | null>(null);

  // Dados Principais
  clientes: ClienteResponse[] = [];
  petsDoClienteParaFormulario: AnimalResponse[] = [];
  clienteIdSelecionado: number | null = null;

  agendamentosFuturos: AgendamentoResponse[] = [];
  agendamentoExibido: AgendamentoResponse | null = null;
  petExibido: AnimalResponse | null = null;
  indiceAgendamentoAtual = 0;
  isLoading = false;

  exibirFormularioCadastroPet = false;
  exibirFormularioAgendamento = false;

  imagemPetUrl: SafeUrl | null = null;
  internacaoAtiva: InternacaoResponse | null = null;

  constructor(
    private clienteService: ClienteService,
    private animalService: AnimalService,
    private agendamentoService: AgendamentoService,
    private internacaoService: InternacaoService,
    private sessionService: SessionService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadClienteLogado();
    this.loadDropdownData();
  }

  logout(): void {
    this.sessionService.logout();
  }

  selecionarAba(aba: 'agendamentos' | 'menu'): void {
    this.abaAtiva.set(aba);
  }

  loadClienteLogado(): void {
    this.clienteService.getClienteLogado().subscribe({
      next: (data) => {
        this.clienteIdSelecionado = data.id;
        this.carregarDadosDashboard(data.id);
        this.carregarPetsParaFormulario(data.id);
        this.checarNotificacaoInternacaoCliente(data.id);
      },
      error: (err) => {
        console.error("ERRO ao buscar cliente logado!", err);
      }
    });
  }

  loadDropdownData(): void {
    this.animalService.getEspecies().subscribe(data => this.especiesSignal.set(data));
    this.animalService.getPortes().subscribe(data => this.portesSignal.set(data));
    this.animalService.getSexos().subscribe(data => this.sexosSignal.set(data));
    this.agendamentoService.getServicos().subscribe(data => this.servicosSignal.set(data));
  }

  onClienteChange(clienteId: number | null): void {
    this.resetarTudo();
    this.clienteIdSelecionado = clienteId;
    if (clienteId) {
      this.carregarDadosDashboard(clienteId);
      this.carregarPetsParaFormulario(clienteId);
      this.checarNotificacaoInternacaoCliente(clienteId);
    }
  }

  carregarDadosDashboard(clienteId: number): void {
    this.isLoading = true;
    this.agendamentoService.getAgendamentos().subscribe({
      next: (todosAgendamentos) => {
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);

        this.agendamentosFuturos = todosAgendamentos
          .filter(ag => ag.cliente.id == clienteId && new Date(ag.dataHoraAgendamento) >= hoje)
          .sort((a, b) => new Date(a.dataHoraAgendamento).getTime() - new Date(b.dataHoraAgendamento).getTime());

        if (this.agendamentosFuturos.length > 0) {
          this.indiceAgendamentoAtual = 0;
          this.atualizarExibicao();
        } else {
          this.carregarPrimeiroPetDoCliente(clienteId);
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("ERRO ao buscar agendamentos!", err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  checarNotificacaoInternacaoCliente(clienteId: number): void {
    this.internacaoAtiva = null;

    this.animalService.getAnimais().pipe(
      switchMap(todosOsPets => {
        const petsDoCliente = todosOsPets.filter(pet => pet.cliente.id === clienteId);
        if (petsDoCliente.length === 0) {
          return of(null);
        }

        const checagens = petsDoCliente.map(pet =>
          this.internacaoService.getInternacaoAtivaPorAnimalId(pet.id).pipe(
            catchError(() => of(null))
          )
        );

        return forkJoin(checagens);
      })
    ).subscribe(resultados => {
      if (resultados) {
        const primeiraInternacao = resultados.find(res => res !== null);
        if (primeiraInternacao) {
          this.internacaoAtiva = primeiraInternacao;
          this.cdr.detectChanges();
        }
      }
    });
  }

  carregarPrimeiroPetDoCliente(clienteId: number): void {
    this.animalService.getAnimais().subscribe(todosOsPets => {
      const petsDoCliente = todosOsPets.filter(pet => pet.cliente.id === clienteId);
      if (petsDoCliente.length > 0) {
        this.petExibido = petsDoCliente[0];
        this.atualizarUrlImagem();
      }
    });
  }

  atualizarExibicao(): void {
    if (this.agendamentosFuturos.length === 0) {
      this.agendamentoExibido = null;
      if (this.clienteIdSelecionado) this.carregarPrimeiroPetDoCliente(this.clienteIdSelecionado);
      return;
    }

    this.agendamentoExibido = this.agendamentosFuturos[this.indiceAgendamentoAtual];
    this.petExibido = this.agendamentoExibido.animal;
    this.atualizarUrlImagem();
  }

  atualizarUrlImagem(): void {
    if (this.petExibido && this.petExibido.id) {
      const url = `${environment.apiUrl}/api/animais/imagem/${this.petExibido.id}`;
      this.imagemPetUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    } else {
      this.imagemPetUrl = null;
    }
  }

  navegarAgendamentos(direcao: number): void {
    const total = this.agendamentosFuturos.length;
    if (total <= 1) return;
    this.indiceAgendamentoAtual = (this.indiceAgendamentoAtual + direcao + total) % total;
    this.atualizarExibicao();
  }

  abrirFormularioCadastro(): void {
    if (this.clienteIdSelecionado) {
      this.exibirFormularioAgendamento = false;
      this.exibirFormularioCadastroPet = true;
      this.petFormErrorMessage.set(null);
      this.petFormSuccessMessage.set(null);
    }
  }

  abrirFormularioAgendamento(): void {
    if (this.clienteIdSelecionado) {
      this.exibirFormularioCadastroPet = false;
      this.exibirFormularioAgendamento = true;
      this.agendamentoFormErrorMessage.set(null);
      this.agendamentoFormSuccessMessage.set(null);
    }
  }

  onSalvarPet(formData: FormData): void {
    this.petFormIsLoading.set(true);
    this.petFormErrorMessage.set(null);
    this.petFormSuccessMessage.set(null);

    this.animalService.createAnimal(formData).subscribe({
      next: () => {
        this.petFormIsLoading.set(false);
        this.petFormSuccessMessage.set('Pet cadastrado com sucesso!');
        setTimeout(() => {
          this.exibirFormularioCadastroPet = false;
          if (this.clienteIdSelecionado) {
            this.onClienteChange(this.clienteIdSelecionado);
          }
        }, 1500);
      },
      error: (err) => {
        this.petFormIsLoading.set(false);
        this.petFormErrorMessage.set('Erro ao cadastrar o pet. Tente novamente.');
        console.error(err);
      }
    });
  }

  onCancelarCadastroPet(): void {
    this.exibirFormularioCadastroPet = false;
  }

  onConfirmarAgendamento(request: AgendamentoRequest): void {
    this.agendamentoFormIsLoading.set(true);
    this.agendamentoFormErrorMessage.set(null);
    this.agendamentoFormSuccessMessage.set(null);

    this.agendamentoService.createAgendamento(request).subscribe({
      next: () => {
        this.agendamentoFormIsLoading.set(false);
        this.agendamentoFormSuccessMessage.set('Agendamento realizado com sucesso!');
        setTimeout(() => {
          this.exibirFormularioAgendamento = false;
          if (this.clienteIdSelecionado) {
            this.carregarDadosDashboard(this.clienteIdSelecionado);
          }
        }, 2000);
      },
      error: (err) => {
        this.agendamentoFormIsLoading.set(false);
        this.agendamentoFormErrorMessage.set(err.error?.message || 'Erro ao realizar o agendamento.');
        console.error(err);
      }
    });
  }

  onFormularioAgendamentoFechado(): void {
    this.exibirFormularioAgendamento = false;
  }

  carregarPetsParaFormulario(clienteId: number): void {
    this.animalService.getAnimais().subscribe(todosOsPets => {
      this.petsDoClienteParaFormulario = todosOsPets.filter(pet => pet.cliente.id == clienteId);
    });
  }

  resetarTudo(): void {
    this.exibirFormularioCadastroPet = false;
    this.exibirFormularioAgendamento = false;
    this.agendamentosFuturos = [];
    this.agendamentoExibido = null;
    this.petExibido = null;
    this.petsDoClienteParaFormulario = [];
    this.imagemPetUrl = null;
    this.indiceAgendamentoAtual = 0;
    this.internacaoAtiva = null;
  }
}
