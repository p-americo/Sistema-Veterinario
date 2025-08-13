// src/app/models/internacao.model.ts
import { MedicamentoResponse } from "./medicamento.model";

// --- ADMINISTRAÇÃO DE MEDICAMENTO ---
export interface AdministracaoMedicamentoRequest {
  diariaId: number; // Assumindo que a medicação é ligada a uma diária
  medicamentoId: number;
  funcionarioExecutorId: number;
  quantidadeAdministrada: number;
  dataHora: string;
  dosagem?: string;
}
export interface AdministracaoMedicamentoResponse {
  id: number;
  nomeMedicamento: string;
  nomeFuncionarioExecutor: string;
  quantidadeAdministrada: number;
  dataHora: string;
  dosagem?: string;
}

// --- DIÁRIA ---
export interface DiariaRequest {
  internacaoId: number;
  dataHora: string;
  pesoNoDia?: number;
  observacoesClinicas?: string;
  diagnostico?: string;
}
export interface DiariaResponse {
  id: number;
  dataHora: string;
  pesoNoDia?: number;
  observacoesClinicas?: string;
  diagnostico?: string;
  medicamentos: AdministracaoMedicamentoResponse[];
}

// --- INTERNAÇÃO ---
export interface InternacaoRequest {
  animalId: number;
  dataEntrada: string;
}
export interface InternacaoResponse {
  id: number;
  animalId: number;
  nomeAnimal?: string; // Adicionaremos para facilitar
  dataEntrada: string;
  dataSaida?: string;
  status: string;
  diarias: DiariaResponse[]; // A internação contém uma lista de diárias
}
