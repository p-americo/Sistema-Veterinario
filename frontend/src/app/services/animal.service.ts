// src/app/services/animal.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnimalRequest, AnimalResponse, AnimalUpdate } from '../models/animal.model';
import { ClienteResponse } from '../models/cliente.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AnimalService {
  private apiUrl = `${environment.apiUrl}/api/animais`;
  private enumsUrl = `${environment.apiUrl}/api/enums`;
  private clientesUrl = `${environment.apiUrl}/api/clientes`;

  constructor(private http: HttpClient) { }


  getEspecies(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/especies`);
  }
  getPortes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/portes`);
  }
  getSexos(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/sexos`);
  }


  getClientes(): Observable<ClienteResponse[]> {
    return this.http.get<ClienteResponse[]>(this.clientesUrl);
  }


  getAnimais(): Observable<AnimalResponse[]> {
    return this.http.get<AnimalResponse[]>(this.apiUrl);
  }

  createAnimal(formData: FormData): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(this.apiUrl, formData);
  }

  updateAnimal(id: number, animal: AnimalUpdate): Observable<AnimalResponse> {
    return this.http.put<AnimalResponse>(`${this.apiUrl}/${id}`, animal);
  }

  deleteAnimal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
