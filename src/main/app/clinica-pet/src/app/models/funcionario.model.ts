// src/app/models/funcionario.model.ts

export interface Cargo {
  id: number;
  cargo: string;
  salario?:number;
}

// Para criar um novo funcionário
export interface FuncionarioRequest {
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  dataAdmissao: string;
  cargoId: number;
  crmv?: string; // O '?' torna o campo opcional
}

// Para atualizar um funcionário existente
export interface FuncionarioUpdate {
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  dataAdmissao: string;
  cargoId: number;
  crmv?: string;
}

// A resposta que recebemos da API
export interface FuncionarioResponse {
  id: number;
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  dataCadastro: string;
  dataAdmissao: string;
  cargo: Cargo;
  crmv?: string;
}
