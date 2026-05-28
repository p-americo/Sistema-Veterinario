// src/app/services/funcionario.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FuncionarioRequest, FuncionarioResponse, FuncionarioUpdate } from '../models/funcionario.model';
import { Cargo } from '../models/funcionario.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FuncionarioService {
  private apiUrl = `${environment.apiUrl}/api/funcionarios`;
  private cargoApiUrl = `${environment.apiUrl}/api/cargos`;

  constructor(private http: HttpClient) { }

  getCargos(): Observable<Cargo[]> {
    return this.http.get<Cargo[]>(this.cargoApiUrl);
  }

  getFuncionarios(): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(this.apiUrl);
  }


  getFuncionarioById(id: number): Observable<FuncionarioResponse> {
    return this.http.get<FuncionarioResponse>(`${this.apiUrl}/${id}`);
  }


  searchFuncionariosByName(nome: string): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(`${this.apiUrl}/nome/${nome}`);
  }


  createFuncionario(funcionario: FuncionarioRequest): Observable<FuncionarioResponse> {
    return this.http.post<FuncionarioResponse>(this.apiUrl, funcionario);
  }


  updateFuncionario(id: number, funcionario: FuncionarioUpdate): Observable<FuncionarioResponse> {
    return this.http.put<FuncionarioResponse>(`${this.apiUrl}/${id}`, funcionario);
  }


  deleteFuncionario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
