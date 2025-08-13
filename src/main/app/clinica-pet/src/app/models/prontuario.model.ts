
import { AnimalResponse } from './animal.model';

export interface AdministracaoMedicamentoResponse {
  id: number;
  nomeMedicamento: string;
  dosagem: string;
  dataHora: string;
}

export interface RegistroProntuarioResponse {
  id: number;
  dataRegistro: string;
  dataHora: string;
  descricao: string;
  observacoesClinicas?: string;
  diagnostico?: string;
  pesoNoDia?: number;
  nomeVeterinario: string;
  veterinarioResponsavelId: number;
  medicamentosAdministrados?: AdministracaoMedicamentoResponse[];
}

export interface RegistroProntuarioRequest {
  prontuarioId: number;
  veterinarioResponsavelId: number;
  dataHora: string;
  pesoNoDia?: number;
  observacoesClinicas?: string;
  diagnostico?: string;
  agendamentoId?: number;
}

export interface ProntuarioRequest {
  animalId: number;
  alergiasConhecidas?: string;
  condicoesPreexistentes?: string;
}

export interface ProntuarioResponse {
  id: number;
  animalId: number;
  nomeAnimal: string;
  alergiasConhecidas?: string;
  condicoesPreexistentes?: string;
  registros: RegistroProntuarioResponse[];
}
