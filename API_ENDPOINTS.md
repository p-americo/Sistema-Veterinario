# Sistema de Prontuário Digital - API Endpoints

Este documento descreve os endpoints implementados para o sistema de prontuário digital da clínica veterinária.

## Prontuários (`/api/prontuarios`)

### POST `/api/prontuarios` - Criar Prontuário
Cria um novo prontuário para um animal.

**Request Body:**
```json
{
    "animalId": 1,
    "alergiasConhecidas": "Nenhuma alergia conhecida",
    "condicoesPreexistentes": "Nenhuma condição preexistente"
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "animalId": 1,
    "nomeAnimal": "Rex",
    "alergiasConhecidas": "Nenhuma alergia conhecida",
    "condicoesPreexistentes": "Nenhuma condição preexistente",
    "registros": []
}
```

### GET `/api/prontuarios` - Listar Todos os Prontuários
### GET `/api/prontuarios/{id}` - Buscar Prontuário por ID
### GET `/api/prontuarios/{id}/completo` - Buscar Prontuário com Registros
### GET `/api/prontuarios/animal/{animalId}` - Buscar Prontuário por Animal ID
### PUT `/api/prontuarios/{id}` - Atualizar Prontuário
### DELETE `/api/prontuarios/{id}` - Deletar Prontuário

## Registros de Prontuário (`/api/registros-prontuario`)

### POST `/api/registros-prontuario` - Criar Registro
Cria um novo registro no prontuário.

**Request Body:**
```json
{
    "prontuarioId": 1,
    "agendamentoId": 1,
    "veterinarioResponsavelId": 1,
    "dataHora": "2024-01-15T10:30:00",
    "internacaoId": null,
    "pesoNoDia": 25.5,
    "observacoesClinicas": "Animal apresenta bom estado geral...",
    "diagnostico": "Consulta de rotina - animal saudável"
}
```

### GET endpoints para buscar registros por:
- `/api/registros-prontuario` - Todos os registros
- `/api/registros-prontuario/{id}` - Por ID
- `/api/registros-prontuario/{id}/medicamentos` - Com medicamentos administrados
- `/api/registros-prontuario/prontuario/{prontuarioId}` - Por prontuário
- `/api/registros-prontuario/veterinario/{veterinarioId}` - Por veterinário
- `/api/registros-prontuario/internacao/{internacaoId}` - Por internação
- `/api/registros-prontuario/periodo?inicio=2024-01-01T00:00:00&fim=2024-01-31T23:59:59` - Por período

### PUT `/api/registros-prontuario/{id}` - Atualizar Registro
### DELETE `/api/registros-prontuario/{id}` - Deletar Registro

## Administração de Medicamentos (`/api/administracoes-medicamento`)

### POST `/api/administracoes-medicamento` - Registrar Administração
**Request Body:**
```json
{
    "medicamentoId": 1,
    "entradaProntuarioId": 1,
    "funcionarioExecutorId": 1,
    "quantidadeAdministrada": 2.5,
    "dataHora": "2024-01-15T11:00:00",
    "dosagem": "5mg/ml"
}
```

### GET endpoints para buscar administrações por:
- `/api/administracoes-medicamento` - Todas as administrações
- `/api/administracoes-medicamento/{id}` - Por ID
- `/api/administracoes-medicamento/entrada-prontuario/{entradaProntuarioId}` - Por entrada do prontuário
- `/api/administracoes-medicamento/medicamento/{medicamentoId}` - Por medicamento
- `/api/administracoes-medicamento/funcionario/{funcionarioId}` - Por funcionário
- `/api/administracoes-medicamento/periodo?inicio=...&fim=...` - Por período

## Internações (`/api/internacoes`)

### POST `/api/internacoes` - Criar Internação
**Request Body:**
```json
{
    "animalId": 1,
    "veterinarioResponsavelId": 1,
    "dataInicio": "2024-01-15T08:00:00",
    "dataFim": null,
    "motivoInternacao": "Cirurgia de emergência",
    "observacoes": "Animal necessita observação pós-cirúrgica",
    "ativa": true
}
```

### GET endpoints para buscar internações:
- `/api/internacoes` - Todas as internações
- `/api/internacoes/{id}` - Por ID
- `/api/internacoes/ativas` - Apenas internações ativas
- `/api/internacoes/periodo?inicio=...&fim=...` - Por período

### PUT `/api/internacoes/{id}` - Atualizar Internação
### PUT `/api/internacoes/{id}/finalizar` - Finalizar Internação (define dataFim e ativa=false)
### DELETE `/api/internacoes/{id}` - Deletar Internação

## Funcionalidades Implementadas

1. **CRUD completo** para todas as entidades do prontuário digital
2. **Relacionamentos apropriados** entre as entidades
3. **Validação de dados** usando Bean Validation
4. **Tratamento de erros** com exceções personalizadas
5. **Consultas específicas** para diferentes cenários de uso
6. **Integração com internação** - registros podem ser marcados como parte de uma internação

## Estratégia de Internação

Conforme solicitado, a internação utiliza a mesma estratégia do prontuário:
- As informações da internação são preenchidas no prontuário
- Uma flag (campo `internacaoId`) no registro marca que estava em internação
- Permite rastreamento completo do tratamento durante a internação

## Notas sobre TipoMedicamento

A implementação atual inclui uma estrutura básica para `TipoMedicamento` que pode ser expandida pela outra equipe. Os campos básicos incluem:
- Nome, descrição, preço (herdados de TipoProduto)
- Categoria, via de administração, dosagem padrão
- Princípio ativo, fabricante, lote
- Flag para prescrição obrigatória

## Próximos Passos

1. Configurar banco de dados para testes completos
2. Implementar autenticação e autorização
3. Adicionar mais validações de negócio específicas
4. Expandir a funcionalidade de medicamentos quando a outra equipe implementar
5. Adicionar relatórios e dashboards