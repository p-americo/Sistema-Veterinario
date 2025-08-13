// src/app/services/prontuario.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProntuarioRequest, ProntuarioResponse, RegistroProntuarioRequest } from '../models/prontuario.model';
import { AnimalResponse } from '../models/animal.model';
import { FuncionarioResponse } from '../models/funcionario.model';

@Injectable({ providedIn: 'root' })
export class ProntuarioService {
  private apiUrl = 'http://localhost:8080/api/prontuarios';
  private registrosApiUrl = 'http://localhost:8080/api/registros-prontuario'; // URL para os registros
  private animaisUrl = 'http://localhost:8080/api/animais';
  private veterinariosUrl = 'http://localhost:8080/api/funcionarios/veterinarios';

  constructor(private http: HttpClient) { }

  // ... Métodos de Prontuário ...
  getProntuarioByAnimalId(animalId: number): Observable<ProntuarioResponse> {
    return this.http.get<ProntuarioResponse>(`${this.apiUrl}/animal/${animalId}`);
  }
  createProntuario(prontuario: ProntuarioRequest): Observable<ProntuarioResponse> {
    return this.http.post<ProntuarioResponse>(this.apiUrl, prontuario);
  }
  updateProntuario(id: number, prontuario: ProntuarioRequest): Observable<ProntuarioResponse> {
    return this.http.put<ProntuarioResponse>(`${this.apiUrl}/${id}`, prontuario);
  }

  // Métodos para popular Dropdowns
  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>(this.animaisUrl);
  }
  getVeterinarios(): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(this.veterinariosUrl);
  }

  // --- MÉTODOS NOVOS PARA REGISTROS ---
  createRegistroProntuario(registro: RegistroProntuarioRequest): Observable<ProntuarioResponse> {
    return this.http.post<ProntuarioResponse>(this.registrosApiUrl, registro);
  }
  updateRegistroProntuario(id: number, registro: RegistroProntuarioRequest): Observable<ProntuarioResponse> {
    return this.http.put<ProntuarioResponse>(`${this.registrosApiUrl}/${id}`, registro);
  }
  deleteRegistroProntuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.registrosApiUrl}/${id}`);
  }
}
