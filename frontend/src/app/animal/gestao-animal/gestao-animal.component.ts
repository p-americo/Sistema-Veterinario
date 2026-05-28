import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AnimalService } from '../../services/animal.service';
import { AnimalResponse } from '../../models/animal.model';
import { ClienteResponse } from '../../models/cliente.model';

@Component({
  selector: 'app-gestao-animal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './gestao-animal.html',
})
export class GestaoAnimalComponent implements OnInit {
  animais: AnimalResponse[] = [];
  listaClientes: ClienteResponse[] = [];
  listaEspecies: string[] = [];
  listaPortes: string[] = [];
  listaSexos: string[] = [];

  animalForm: FormGroup;
  isEditing = false;
  currentAnimalId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private animalService: AnimalService,
    private fb: FormBuilder
  ) {
    this.animalForm = this.fb.group({
      clienteId: [null, Validators.required],
      nome: ['', [Validators.required, Validators.maxLength(100)]],
      especie: [null, Validators.required],
      porte: [null, Validators.required],
      sexo: [null, Validators.required],
      raca: ['', [Validators.required, Validators.maxLength(100)]],
      cor: ['', [Validators.maxLength(100)]],
      peso: [null, [Validators.required, Validators.min(0.01)]],
      dataNascimento: ['', Validators.required],
      castrado: [false, Validators.required],
      observacao: ['']
    });
  }

  ngOnInit(): void {
    this.loadAnimais();
    this.loadDropdownData();
  }

  loadAnimais(): void {
    this.animalService.getAnimais().subscribe({
      next: (data) => {
        this.animais = data;
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar animais: ' + error.message;
      }
    });
  }

  loadDropdownData(): void {
    this.animalService.getClientes().subscribe({
      next: (data) => {
        this.listaClientes = data;
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar clientes: ' + error.message;
      }
    });

    this.animalService.getEspecies().subscribe({
      next: (data) => {
        this.listaEspecies = data;
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar espécies: ' + error.message;
      }
    });

    this.animalService.getPortes().subscribe({
      next: (data) => {
        this.listaPortes = data;
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar portes: ' + error.message;
      }
    });

    this.animalService.getSexos().subscribe({
      next: (data) => {
        this.listaSexos = data;
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar sexos: ' + error.message;
      }
    });
  }

  onSubmit(): void {
    if (this.animalForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos corretamente.';
      return;
    }

    const formValues = this.animalForm.value;

    if (this.isEditing && this.currentAnimalId) {
      const animalUpdate = {
        nome: formValues.nome,
        especie: formValues.especie,
        porte: formValues.porte,
        sexo: formValues.sexo,
        raca: formValues.raca,
        cor: formValues.cor,
        peso: formValues.peso,
        castrado: formValues.castrado,
        dataNascimento: formValues.dataNascimento,
        observacao: formValues.observacao,
        clienteId: Number(formValues.clienteId)
      };

      this.animalService.updateAnimal(this.currentAnimalId, animalUpdate).subscribe({
        next: () => {
          this.successMessage = 'Animal atualizado com sucesso!';
          this.resetForm();
          this.loadAnimais();
        },
        error: (error) => {
          this.errorMessage = 'Erro ao atualizar animal: ' + error.message;
        }
      });
    } else {
      const animalDataParaJson = {
        nome: formValues.nome,
        especie: formValues.especie,
        porte: formValues.porte,
        sexo: formValues.sexo,
        raca: formValues.raca,
        cor: formValues.cor,
        peso: formValues.peso,
        castrado: formValues.castrado,
        dataNascimento: formValues.dataNascimento,
        observacao: formValues.observacao,
        clienteId: Number(formValues.clienteId)
      };

      const formData = new FormData();
      formData.append('dados', new Blob([JSON.stringify(animalDataParaJson)], { type: 'application/json' }));
      
      // Criar um arquivo dummy para a foto já que não há input de imagem neste formulário de gestão
      const dummyBlob = new Blob([''], { type: 'image/png' });
      const dummyFile = new File([dummyBlob], 'placeholder.png', { type: 'image/png' });
      formData.append('imagem', dummyFile, dummyFile.name);

      this.animalService.createAnimal(formData).subscribe({
        next: () => {
          this.successMessage = 'Animal criado com sucesso!';
          this.resetForm();
          this.loadAnimais();
        },
        error: (error) => {
          this.errorMessage = 'Erro ao criar animal: ' + error.message;
        }
      });
    }
  }

  editAnimal(animal: AnimalResponse): void {
    this.isEditing = true;
    this.currentAnimalId = animal.id;

    // Format date from ISO string/date to YYYY-MM-DD for input[type=date]
    const dataNacimentoFormatada = animal.dataNascimento ? animal.dataNascimento.split('T')[0] : '';

    this.animalForm.patchValue({
      clienteId: animal.cliente ? animal.cliente.id : animal.clienteId,
      nome: animal.nome,
      especie: animal.especie,
      porte: animal.porte,
      sexo: animal.sexo,
      raca: animal.raca,
      cor: animal.cor,
      peso: animal.peso,
      dataNascimento: dataNacimentoFormatada,
      castrado: animal.castrado,
      observacao: animal.observacao
    });
  }

  deleteAnimal(id: number): void {
    if (confirm('Tem certeza que deseja excluir este animal?')) {
      this.animalService.deleteAnimal(id).subscribe({
        next: () => {
          this.successMessage = 'Animal excluído com sucesso!';
          this.loadAnimais();
        },
        error: (error) => {
          this.errorMessage = 'Erro ao excluir animal: ' + error.message;
        }
      });
    }
  }

  resetForm(): void {
    this.animalForm.reset({
      castrado: false
    });
    this.isEditing = false;
    this.currentAnimalId = null;
    this.errorMessage = '';
    this.successMessage = '';
  }
}
