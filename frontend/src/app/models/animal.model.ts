// src/app/models/animal.model.ts


import { ClienteResponse } from './cliente.model';

export interface AnimalRequest {
  nome: string;
  especie: string;
  porte: string;
  raca: string;
  sexo: string;
  cor?: string;
  peso: number;
  castrado: boolean;
  dataNascimento: string;
  observacao?: string;
  clienteId: number;
}

export interface AnimalUpdate {
  // Similar ao Request
  nome: string;
  especie: string;
  porte: string;
  raca: string;
  sexo: string;
  cor?: string;
  peso: number;
  castrado: boolean;
  dataNascimento: string;
  observacao?: string;
  clienteId: number;
}

export interface AnimalResponse {
  id: number;
  nome: string;
  especie: string;
  porte: string;
  raca: string;
  sexo: string;
  cor?: string;
  peso: number;
  castrado: boolean;
  dataNascimento: string;
  observacao?: string;
  cliente: ClienteResponse;
  clienteId: number;
}
