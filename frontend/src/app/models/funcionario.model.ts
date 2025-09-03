// src/app/models/funcionario.model.ts

export interface Cargo {
  id: number;
  cargo: string;
  salario?:number;
}


export interface FuncionarioRequest {
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  dataAdmissao: string;
  cargoId: number;
  crmv?: string;
}


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
