export interface ClienteRequest {
  nome: string;
  cpf: string;
  dataNascimento: string; // ISO format date string
  telefone: string;
  email: string;
}

export interface ClienteResponse {
  id: number;
  nome: string;
  cpf: string;
  dataNascimento: string; // ISO format date string
  telefone: string;
  email: string;
  dataCadastro: string; // ISO format date string
}

export interface ClienteUpdate {
  nome: string;
  cpf: string;
  dataNascimento: string; // ISO format date string
  telefone: string;
  email: string;
}
