// src/app/services/medicamento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MedicamentoRequest, MedicamentoResponse } from '../models/medicamento.model';

@Injectable({ providedIn: 'root' })
export class MedicamentoService {
  private apiUrl = 'http://localhost:8080/api/medicamentos';
  private enumsUrl = 'http://localhost:8080/api/enums';

  constructor(private http: HttpClient) { }

  // CRUD para Medicamentos
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

  // Métodos para popular os Dropdowns
  getCategorias(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/medicamento-categorias`);
  }
  getViasAdministracao(): Observable<string[]> {
    return this.http.get<string[]>(`${this.enumsUrl}/medicamento-vias`);
  }
}
