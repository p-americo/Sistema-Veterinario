// src/app/models/internacao.model.ts
import { MedicamentoResponse } from "./medicamento.model";


export interface AdministracaoMedicamentoRequest {
  diariaId: number;
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


export interface InternacaoRequest {
  animalId: number;
  dataEntrada: string;
}
export interface InternacaoResponse {
  id: number;
  animalId: number;
  nomeAnimal?: string;
  dataEntrada: string;
  dataSaida?: string;
  status: string;
  diarias: DiariaResponse[];
}
