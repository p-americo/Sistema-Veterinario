export interface ClienteRequest {
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
}

export interface ClienteResponse {
  id: number;
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  dataCadastro: string;
}

export interface ClienteUpdate {
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
}
