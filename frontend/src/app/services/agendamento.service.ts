// src/app/services/agendamento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AgendamentoRequest, AgendamentoResponse } from '../models/agendamento.model';
import { ClienteResponse } from '../models/cliente.model';
import { AnimalResponse } from '../models/animal.model';
import { ServicoResponse } from '../models/servico.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AgendamentoService {
  private apiUrl = `${environment.apiUrl}/api/agendamentos`;

  constructor(private http: HttpClient) { }


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


  getClientes(): Observable<ClienteResponse[]> {
    return this.http.get<ClienteResponse[]>(`${environment.apiUrl}/api/clientes`);
  }
  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>(`${environment.apiUrl}/api/animais`);
  }
  getServicos(): Observable<ServicoResponse[]> {

    return this.http.get<ServicoResponse[]>(`${environment.apiUrl}/api/servicos`);
  }
  getAgendamentoStatus(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/api/enums/agendamento-status`);
  }
}
