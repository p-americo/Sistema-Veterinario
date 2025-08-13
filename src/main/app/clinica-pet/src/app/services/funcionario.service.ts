// src/app/services/funcionario.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FuncionarioRequest, FuncionarioResponse, FuncionarioUpdate } from '../models/funcionario.model';
import { Cargo } from '../models/funcionario.model';

@Injectable({
  providedIn: 'root'
})
export class FuncionarioService {
  private apiUrl = 'http://localhost:8080/api/funcionarios'; // <-- URL ATUALIZADA
  private cargoApiUrl = 'http://localhost:8080/api/cargos';

  constructor(private http: HttpClient) { }

  getCargos(): Observable<Cargo[]> {
    return this.http.get<Cargo[]>(this.cargoApiUrl);
  }
  // Buscar todos os funcionários
  getFuncionarios(): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(this.apiUrl);
  }

  // Buscar funcionário por ID
  getFuncionarioById(id: number): Observable<FuncionarioResponse> {
    return this.http.get<FuncionarioResponse>(`${this.apiUrl}/${id}`);
  }

  // Buscar funcionários por nome
  searchFuncionariosByName(nome: string): Observable<FuncionarioResponse[]> {
    return this.http.get<FuncionarioResponse[]>(`${this.apiUrl}/nome/${nome}`);
  }

  // Criar novo funcionário
  createFuncionario(funcionario: FuncionarioRequest): Observable<FuncionarioResponse> {
    return this.http.post<FuncionarioResponse>(this.apiUrl, funcionario);
  }

  // Atualizar funcionário
  updateFuncionario(id: number, funcionario: FuncionarioUpdate): Observable<FuncionarioResponse> {
    return this.http.put<FuncionarioResponse>(`${this.apiUrl}/${id}`, funcionario);
  }

  // Excluir funcionário
  deleteFuncionario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
