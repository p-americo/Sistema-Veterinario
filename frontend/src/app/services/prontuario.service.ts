// src/app/services/prontuario.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProntuarioRequest, ProntuarioResponse, RegistroProntuarioRequest, RegistroProntuarioResponse } from '../models/prontuario.model';
import { AnimalResponse } from '../models/animal.model';
import { FuncionarioResponse } from '../models/funcionario.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProntuarioService {
  private apiUrl = `${environment.apiUrl}/api/prontuarios`;
  private registrosApiUrl = `${environment.apiUrl}/api/registros-prontuario`; // URL para os registros
  private animaisUrl = `${environment.apiUrl}/api/animais`;
  private veterinariosUrl = `${environment.apiUrl}/api/funcionarios/veterinarios`;

  constructor(private http: HttpClient) { }


  getProntuarioByAnimalId(animalId: number): Observable<ProntuarioResponse> {
    return this.http.get<ProntuarioResponse>(`${this.apiUrl}/animal/${animalId}`);
  }
  createProntuario(prontuario: ProntuarioRequest): Observable<ProntuarioResponse> {
    return this.http.post<ProntuarioResponse>(this.apiUrl, prontuario);
  }
  updateProntuario(id: number, prontuario: ProntuarioRequest): Observable<ProntuarioResponse> {
    return this.http.put<ProntuarioResponse>(`${this.apiUrl}/${id}`, prontuario);
  }


  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>(this.animaisUrl);
  }
  getVeterinarios(): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(this.veterinariosUrl);
  }


  createRegistroProntuario(registro: RegistroProntuarioRequest): Observable<RegistroProntuarioResponse> {
    return this.http.post<RegistroProntuarioResponse>(this.registrosApiUrl, registro);
  }
  updateRegistroProntuario(id: number, registro: RegistroProntuarioRequest): Observable<RegistroProntuarioResponse> {
    return this.http.put<RegistroProntuarioResponse>(`${this.registrosApiUrl}/${id}`, registro);
  }
  deleteRegistroProntuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.registrosApiUrl}/${id}`);
  }
}
