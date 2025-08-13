// src/app/models/medicamento.model.ts

export interface MedicamentoRequest {
  nome: string;
  descricao?: string;
  quantidadeEstoque: number;
  categoria: string;
  viaAdministracao?: string;
  dosagemPadrao?: string;
  principioAtivo?: string;
  fabricante?: string;
  prescricaoObrigatoria: boolean;
}

export interface MedicamentoResponse {
  id: number;
  nome: string;
  descricao?: string;
  quantidadeEstoque: number;
  categoria: string;
  viaAdministracao?: string;
  dosagemPadrao?: string;
  principioAtivo?: string;
  prescricaoObrigatoria: boolean;
}
