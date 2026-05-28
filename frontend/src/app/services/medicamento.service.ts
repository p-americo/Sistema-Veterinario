// src/app/services/medicamento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MedicamentoRequest, MedicamentoResponse } from '../models/medicamento.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MedicamentoService {
  private apiUrl = `${environment.apiUrl}/api/medicamentos`;
  private enumsUrl = `${environment.apiUrl}/api/enums`;

  constructor(private http: HttpClient) { }


  getMedicamentos(): Observable<MedicamentoResponse[]> {
    return this.http.get<MedicamentoResponse[]>(this.apiUrl);
  }
  createMedicamento(medicamento: MedicamentoRequest): Observable<MedicamentoResponse> {
    return this.http.post<MedicamentoResponse>(this.apiUrl, medicamento);
  }
  updateMedicamento(id: number, medicamento: MedicamentoRequest): Observable<MedicamentoResponse> {
    return this.http.put<MedicamentoResponse>(`${this.apiUrl}/${id}`, medicamento);
  }
  deleteMedicamento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }


  getCategorias(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/medicamento-categorias`);
  }
  getViasAdministracao(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/medicamento-vias`);
  }
}
