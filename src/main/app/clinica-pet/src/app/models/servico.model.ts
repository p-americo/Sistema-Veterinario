export interface ServicoRequest {
  tipo: string;
  veterinarioId: number;
  valor: number;
}

export interface ServicoResponse {
  id: number;
  tipo: string;
  valor: number;
  nomeVeterinario: string;
  veterinarioId: number; // <-- Adicionado para a função de editar
}
