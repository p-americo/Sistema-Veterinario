import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { finalize } from 'rxjs/operators';

// Imports dos componentes filhos
import { CadastroFuncionarioComponent } from '../cadastro-funcionario/cadastro-funcionario.component';
import { CadastroServicoComponent } from '../cadastro-servico/cadastro-servico.component';
import { CadastroMedicamentoComponent } from '../cadastro-medicamento/cadastro-medicamento.component';

// --- SERVIÇOS E MODELOS ---
import { ProntuarioService } from '../services/prontuario.service';
import { InternacaoService } from '../services/internacao.service';
import { AnimalResponse } from '../models/animal.model';
import { ProntuarioRequest, ProntuarioResponse, RegistroProntuarioRequest } from '../models/prontuario.model';
import { FuncionarioResponse } from '../models/funcionario.model';
import { DiariaRequest, DiariaResponse, InternacaoRequest, InternacaoResponse, AdministracaoMedicamentoRequest } from '../models/internacao.model';
import { MedicamentoResponse } from '../models/medicamento.model';

@Component({
  selector: 'app-menu-admin',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    CadastroFuncionarioComponent, CadastroServicoComponent, CadastroMedicamentoComponent
  ],
  templateUrl: './menu-admin.component.html',
  styleUrls: ['./menu-admin.component.css']
})
export class MenuAdminComponent implements OnInit {
  // Variáveis de estado da UI
  abaAtiva: 'prontuario' | 'cadastros' = 'prontuario';
  telaCadastroAtiva: 'nenhuma' | 'funcionario' | 'servico' | 'medicamento' = 'nenhuma';
  isLoading = false;
  isSaving = false;
  error: string | null = null;

  // Variáveis de dados
  animais: AnimalResponse[] = [];
  veterinarios: FuncionarioResponse[] = [];
  funcionarios: FuncionarioResponse[] = []; // Para todos os funcionários
  medicamentos: MedicamentoResponse[] = []; // Para a lista de medicamentos
  animalSelecionadoId: number | null = null;
  prontuarioSelecionado: ProntuarioResponse | null = null;
  internacaoAtiva: InternacaoResponse | null = null;
  isLoadingInternacao = false;
  imagemAnimalUrl: SafeUrl | null = null; // Variável para a imagem

  // Variáveis para os formulários e confirmações
  confirmandoInicioInternacao = false;
  exibindoFormularioNovoProntuario = false;
  exibindoFormularioNovoRegistro = false;
  exibindoFormularioNovaDiaria = false;
  exibindoFormularioAddMedicamento = false;
  novoProntuarioRequest!: ProntuarioRequest;
  novoRegistroRequest!: RegistroProntuarioRequest;
  novaDiariaRequest!: DiariaRequest;
  novoMedicamentoRequest!: AdministracaoMedicamentoRequest;

  constructor(
    private prontuarioService: ProntuarioService,
    private internacaoService: InternacaoService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregarAnimais();
    this.carregarVeterinarios();
    this.carregarFuncionarios();
    this.carregarMedicamentos();
  }

  // --- MÉTODOS DE ADMINISTRAÇÃO DE MEDICAMENTO ---
  iniciarAdicionarMedicamento(): void {
    if (this.ultimaDiaria) {
      this.novoMedicamentoRequest = {
        diariaId: this.ultimaDiaria.id,
        medicamentoId: 0,
        funcionarioExecutorId: 0,
        quantidadeAdministrada: 1,
        dosagem: '',
        dataHora: new Date().toISOString()
      };
      this.exibindoFormularioAddMedicamento = true;
    }
  }

  cancelarAdicionarMedicamento(): void {
    this.exibindoFormularioAddMedicamento = false;
  }

  salvarAdicionarMedicamento(): void {
    if (!this.novoMedicamentoRequest.medicamentoId || !this.novoMedicamentoRequest.funcionarioExecutorId) {
      alert('Por favor, selecione o medicamento e o funcionário executor.');
      return;
    }
    this.isSaving = true;
    this.novoMedicamentoRequest.dataHora = new Date().toISOString();

    this.internacaoService.createAdministracaoMedicamento(this.novoMedicamentoRequest)
      .pipe(finalize(() => this.isSaving = false))
      .subscribe({
        next: (internacaoAtualizada) => {
          this.internacaoAtiva = internacaoAtualizada;
          this.exibindoFormularioAddMedicamento = false;
        },
        error: (err) => {
          this.error = "Falha ao salvar a administração do medicamento.";
          console.error(err);
        }
      });
  }


  // --- MÉTODOS DE INTERNAÇÃO E DIÁRIA ---
  solicitarConfirmacaoInternacao(): void { this.confirmandoInicioInternacao = true; }
  cancelarConfirmacaoInternacao(): void { this.confirmandoInicioInternacao = false; }
  executarInicioInternacao(): void {
    if (!this.animalSelecionadoId) return;
    this.isSaving = true;
    this.confirmandoInicioInternacao = false;
    const request: InternacaoRequest = { animalId: this.animalSelecionadoId, dataEntrada: new Date().toISOString() };
    this.internacaoService.iniciarInternacao(request)
      .pipe(finalize(() => this.isSaving = false))
      .subscribe({
        next: (novaInternacao) => { this.internacaoAtiva = novaInternacao; },
        error: (err) => { this.error = 'Ocorreu um erro ao iniciar a internação.'; console.error(err); }
      });
  }
  iniciarNovaDiaria(): void {
    if (this.internacaoAtiva) {
      this.novaDiariaRequest = { internacaoId: this.internacaoAtiva.id, dataHora: new Date().toISOString(), pesoNoDia: undefined, observacoesClinicas: '', diagnostico: '' };
      this.exibindoFormularioNovaDiaria = true;
    }
  }
  cancelarNovaDiaria(): void { this.exibindoFormularioNovaDiaria = false; }
  salvarNovaDiaria(): void {
    this.isSaving = true;
    this.novaDiariaRequest.dataHora = new Date().toISOString();
    this.internacaoService.createDiaria(this.novaDiariaRequest)
      .pipe(finalize(() => this.isSaving = false))
      .subscribe({
        next: (internacaoAtualizada) => { this.internacaoAtiva = internacaoAtualizada; this.exibindoFormularioNovaDiaria = false; },
        error: (err) => { this.error = "Falha ao salvar a diária."; console.error(err); }
      });
  }
  verificarInternacaoAtiva(animalId: number): void {
    this.isLoadingInternacao = true;
    this.internacaoAtiva = null;
    this.internacaoService.getInternacaoAtivaPorAnimalId(animalId)
      .pipe(finalize(() => this.isLoadingInternacao = false))
      .subscribe({
        next: (data) => {
          // ===== MUDANÇA APLICADA AQUI =====
          // Verificamos se os dados e a lista de diárias existem.
          if (data && data.diarias) {
            // Ordenamos o array de diárias pela data, da mais nova para a mais antiga.
            data.diarias.sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime());
          }
          this.internacaoAtiva = data;
        },
        error: (err) => { if (err.status !== 404) console.error(err); }
      });
  }

  // --- MÉTODOS PRINCIPAIS ---
  onAnimalChange(): void {
    this.exibindoFormularioNovoProntuario = false;
    this.exibindoFormularioNovoRegistro = false;
    this.exibindoFormularioNovaDiaria = false;
    this.exibindoFormularioAddMedicamento = false;
    this.prontuarioSelecionado = null;
    this.internacaoAtiva = null;
    this.error = null;
    this.confirmandoInicioInternacao = false;

    this.imagemAnimalUrl = null;
    if (this.animalSelecionadoId) {
      const url = `http://localhost:8080/api/animais/imagem/${this.animalSelecionadoId}`;
      this.imagemAnimalUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    }
    this.cdr.detectChanges();

    if (!this.animalSelecionadoId) return;

    this.isLoading = true;
    this.prontuarioService.getProntuarioByAnimalId(this.animalSelecionadoId)
      .pipe(finalize(() => { this.isLoading = false; }))
      .subscribe({
        next: (data) => { this.prontuarioSelecionado = data; },
        error: (err) => { if (err.status !== 404) this.error = 'Falha ao buscar o prontuário.'; }
      });
    this.verificarInternacaoAtiva(this.animalSelecionadoId);
  }
  carregarAnimais(): void {
    this.isLoading = true;
    this.prontuarioService.getAnimais()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (data) => { this.animais = data; },
        error: (err) => { this.error = 'Falha ao carregar a lista de animais.'; console.error(err); }
      });
  }
  carregarVeterinarios(): void {
    this.prontuarioService.getVeterinarios().subscribe({
      next: (data) => { this.veterinarios = data; },
      error: (err) => { console.error('Falha ao carregar veterinários', err); this.error = "Não foi possível carregar a lista de veterinários." }
    });
  }
  carregarFuncionarios(): void {
    this.internacaoService.getFuncionarios().subscribe({
      next: (data) => this.funcionarios = data,
      error: (err) => console.error('Falha ao carregar funcionários', err)
    });
  }
  carregarMedicamentos(): void {
    this.internacaoService.getMedicamentos().subscribe({
      next: (data) => this.medicamentos = data,
      error: (err) => console.error('Falha ao carregar medicamentos', err)
    });
  }
  iniciarNovoProntuario(): void {
    if (this.animalSelecionadoId) {
      this.novoProntuarioRequest = { animalId: this.animalSelecionadoId, alergiasConhecidas: '', condicoesPreexistentes: '' };
      this.exibindoFormularioNovoProntuario = true;
    }
  }
  cancelarNovoProntuario(): void { this.exibindoFormularioNovoProntuario = false; }
  salvarNovoProntuario(): void {
    if (!this.animalSelecionadoId) return;
    this.isSaving = true;
    this.prontuarioService.createProntuario(this.novoProntuarioRequest)
      .pipe(finalize(() => this.isSaving = false))
      .subscribe({
        next: (prontuarioCriado) => { this.exibindoFormularioNovoProntuario = false; this.prontuarioSelecionado = prontuarioCriado; },
        error: (err) => { this.error = 'Falha ao salvar o prontuário. Tente novamente.'; console.error(err); }
      });
  }
  iniciarNovoRegistro(): void {
    if (this.prontuarioSelecionado) {
      this.novoRegistroRequest = { prontuarioId: this.prontuarioSelecionado.id, veterinarioResponsavelId: 0, dataHora: new Date().toISOString(), };
      this.exibindoFormularioNovoRegistro = true;
    }
  }
  cancelarNovoRegistro(): void { this.exibindoFormularioNovoRegistro = false; }
  salvarNovoRegistro(): void {
    if (!this.novoRegistroRequest.veterinarioResponsavelId) { alert('Por favor, selecione o veterinário responsável.'); return; }
    this.isSaving = true;
    this.novoRegistroRequest.dataHora = new Date().toISOString();
    this.prontuarioService.createRegistroProntuario(this.novoRegistroRequest)
      .pipe(finalize(() => this.isSaving = false))
      .subscribe({
        next: (prontuarioAtualizado) => { this.exibindoFormularioNovoRegistro = false; this.prontuarioSelecionado = prontuarioAtualizado; },
        error: (err) => { this.error = 'Falha ao salvar o novo registro.'; console.error(err); }
      });
  }
  get nomeAnimalSelecionado(): string {
    const animal = this.animais.find(a => a.id === this.animalSelecionadoId);
    return animal ? animal.nome : 'Animal';
  }
  selecionarAba(aba: 'prontuario' | 'cadastros'): void { this.abaAtiva = aba; this.telaCadastroAtiva = 'nenhuma'; }
  mostrarCadastroFuncionario(): void { this.telaCadastroAtiva = 'funcionario'; }
  mostrarCadastroServico(): void { this.telaCadastroAtiva = 'servico'; }
  mostrarCadastroMedicamento(): void { this.telaCadastroAtiva = 'medicamento'; }
  voltarParaSelecaoDeCadastro(): void { this.telaCadastroAtiva = 'nenhuma'; }
  get ultimaDiaria(): DiariaResponse | null {
    if (!this.internacaoAtiva?.diarias?.length) {
      return null;
    }
    return [...this.internacaoAtiva.diarias].sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime())[0];
  }
}
