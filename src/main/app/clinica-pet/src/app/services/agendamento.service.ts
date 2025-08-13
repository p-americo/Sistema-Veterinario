// src/app/services/agendamento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AgendamentoRequest, AgendamentoResponse } from '../models/agendamento.model';
import { ClienteResponse } from '../models/cliente.model';
import { AnimalResponse } from '../models/animal.model';
import { ServicoResponse } from '../models/servico.model';

@Injectable({ providedIn: 'root' })
export class AgendamentoService {
  private apiUrl = 'http://localhost:8080/api/agendamentos';

  constructor(private http: HttpClient) { }

  // CRUD Agendamentos
  getAgendamentos(): Observable<AgendamentoResponse[]> {
    return this.http.get<AgendamentoResponse[]>(this.apiUrl);
  }
  createAgendamento(agendamento: AgendamentoRequest): Observable<AgendamentoResponse> {
    return this.http.post<AgendamentoResponse>(this.apiUrl, agendamento);
  }
  updateAgendamento(id: number, agendamento: AgendamentoRequest): Observable<AgendamentoResponse> {
    return this.http.put<AgendamentoResponse>(`${this.apiUrl}/${id}`, agendamento);
  }
  deleteAgendamento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Métodos para popular os Dropdowns
  getClientes(): Observable<ClienteResponse[]> {
    return this.http.get<ClienteResponse[]>('http://localhost:8080/api/clientes');
  }
  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>('http://localhost:8080/api/animais');
  }
  getServicos(): Observable<ServicoResponse[]> {
    // ATENÇÃO: Verifique se a URL da sua API de serviços está correta
    return this.http.get<ServicoResponse[]>('http://localhost:8080/api/servicos');
  }
  getAgendamentoStatus(): Observable<string[]> {
    return this.http.get<string[]>('http://localhost:8080/api/enums/agendamento-status');
  }
}
