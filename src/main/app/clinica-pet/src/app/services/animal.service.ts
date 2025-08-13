// src/app/services/animal.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnimalRequest, AnimalResponse, AnimalUpdate } from '../models/animal.model';
import { ClienteResponse } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class AnimalService {
  private apiUrl = 'http://localhost:8080/api/animais';
  private enumsUrl = 'http://localhost:8080/api/enums';
  private clientesUrl = 'http://localhost:8080/api/clientes';

  constructor(private http: HttpClient) { }

  // Métodos para buscar os Enums
  getEspecies(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/especies`);
  }
  getPortes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/portes`);
  }
  getSexos(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/sexos`);
  }

  // Método para buscar os clientes para o dropdown de dono
  getClientes(): Observable<ClienteResponse[]> {
    return this.http.get<ClienteResponse[]>(this.clientesUrl);
  }

  // Métodos CRUD para Animal
  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>(this.apiUrl);
  }
  createAnimal(animal: AnimalRequest): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(this.apiUrl, animal);
  }
  updateAnimal(id: number, animal: AnimalUpdate): Observable<AnimalResponse> {
    return this.http.put<AnimalResponse>(`${this.apiUrl}/${id}`, animal);
  }
  deleteAnimal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
