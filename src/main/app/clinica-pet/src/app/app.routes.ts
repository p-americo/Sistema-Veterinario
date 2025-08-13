

import { Routes } from '@angular/router';
import { GestaoCliente } from './pessoa/Cliente/gestao-cliente/gestao-cliente';
import { GestaoFuncionario } from './pessoa/Funcionario/gestao-funcionario/gestao-funcionario';
import { GestaoAnimal } from './animal/gestao-animal/gestao-animal';
import { GestaoAgendamento } from './agendamento/gestao-agendamento/gestao-agendamento';
import { GestaoServico } from './agendamento/gestao-servico/gestao-servico';
import { GestaoMedicamento } from './medicamentos/gestao-medicamentos/gestao-medicamento';
import { GestaoProntuario } from './prontuario/gestao-prontuario/gestao-prontuario';
import { GestaoInternacao } from './internacao/gestao-internacao/gestao-internacao';

export const routes: Routes = [
  { path: '', redirectTo: 'api/agendamentos', pathMatch: 'full' },
  { path: 'api/clientes', component: GestaoCliente },
  { path: 'api/funcionarios', component: GestaoFuncionario },
  { path: 'api/animais', component: GestaoAnimal },
  { path: 'api/agendamentos', component: GestaoAgendamento },
  { path: 'api/servicos', component: GestaoServico },
  { path: 'api/medicamentos', component: GestaoMedicamento },
  { path: 'api/prontuarios', component: GestaoProntuario },
  { path: 'api/internacoes', component: GestaoInternacao },
  { path: '**', redirectTo: 'api/agendamentos' }
];
