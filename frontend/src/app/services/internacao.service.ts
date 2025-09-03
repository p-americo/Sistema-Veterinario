// src/app/services/internacao.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnimalResponse } from '../models/animal.model';
import { InternacaoRequest, InternacaoResponse, DiariaRequest, AdministracaoMedicamentoRequest } from '../models/internacao.model';
import { MedicamentoResponse } from '../models/medicamento.model';
import { FuncionarioResponse } from '../models/funcionario.model';

@Injectable({ providedIn: 'root' })
export class InternacaoService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }


  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>(`${this.apiUrl}/animais`);
  }
  getMedicamentos(): Observable<MedicamentoResponse[]> {
    return this.http.get<MedicamentoResponse[]>(`${this.apiUrl}/medicamentos`);
  }
  getFuncionarios(): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(`${this.apiUrl}/funcionarios`);
  }


  getInternacaoAtivaPorAnimalId(animalId: number): Observable<InternacaoResponse> {
    return this.http.get<InternacaoResponse>(`${this.apiUrl}/internacoes/animal/${animalId}/ativa`);
  }
  iniciarInternacao(internacao: InternacaoRequest): Observable<InternacaoResponse> {
    return this.http.post<InternacaoResponse>(`${this.apiUrl}/internacoes`, internacao);
  }
  darAltaInternacao(id: number): Observable<InternacaoResponse> {
    return this.http.post<InternacaoResponse>(`${this.apiUrl}/internacoes/${id}/alta`, {});
  }


  createDiaria(diaria: DiariaRequest): Observable<InternacaoResponse> {
    return this.http.post<InternacaoResponse>(`${this.apiUrl}/diarias-internacao`, diaria);
  }


  createAdministracaoMedicamento(admin: AdministracaoMedicamentoRequest): Observable<InternacaoResponse> {
    return this.http.post<InternacaoResponse>(`${this.apiUrl}/admin-medicamentos`, admin);
  }
}
