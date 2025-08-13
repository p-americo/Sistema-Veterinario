// src/app/models/animal.model.ts

// Usaremos a interface ClienteResponse que já deve existir
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
  clienteId: number; // Enviaremos apenas o ID do cliente
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
  clienteId: number;// A API retorna o objeto cliente completo
}
