import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ServicoRequest, ServicoResponse } from '../models/servico.model';
import { FuncionarioResponse } from '../models/funcionario.model';

@Injectable({ providedIn: 'root' })
export class ServicoService {
  private apiUrl = 'http://localhost:8080/api/servicos';

  constructor(private http: HttpClient) { }

  // CRUD para Serviços
  getServicos(): Observable<ServicoResponse[]> {
    return this.http.get<ServicoResponse[]>(this.apiUrl);
  }
  createServico(servico: ServicoRequest): Observable<ServicoResponse> {
    return this.http.post<ServicoResponse>(this.apiUrl, servico);
  }
  updateServico(id: number, servico: ServicoRequest): Observable<ServicoResponse> {
    return this.http.put<ServicoResponse>(`${this.apiUrl}/${id}`, servico);
  }
  deleteServico(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Métodos para popular os Dropdowns
  getServicoTipos(): Observable<string[]> {
    return this.http.get<string[]>('http://localhost:8080/api/enums/servico-tipos');
  }
  getVeterinarios(): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>('http://localhost:8080/api/funcionarios/veterinarios');
  }
}
