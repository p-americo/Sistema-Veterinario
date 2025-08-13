// src/app/models/agendamento.model.ts
import { AnimalResponse } from './animal.model';
import { ClienteResponse } from './cliente.model';
import { ServicoResponse } from './servico.model';

export interface AgendamentoRequest {
  clienteId: number;
  animalId: number;
  servicoId: number;
  dataHoraAgendamento: string; // Formato ISO: "YYYY-MM-DDTHH:mm"
  status: string;
  observacoes?: string;
}

export interface AgendamentoResponse {
  id: number;
  cliente: ClienteResponse;
  animal: AnimalResponse;
  servico: ServicoResponse;
  dataHoraAgendamento: string;
  status: string;
  observacoes?: string;
}
