import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AnimalService } from '../../services/animal.service';
import { AnimalResponse } from '../../models/animal.model';
import { ClienteResponse } from '../../models/cliente.model';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-gestao-animal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, HttpClientModule],
  templateUrl: './gestao-animal.html',
  //styleUrl: './gestao-animal.css'
})
export class GestaoAnimal implements OnInit {
  animais: AnimalResponse[] = [];
  animalForm: FormGroup;
  isEditing = false;
  currentAnimalId: number | null = null;
  errorMessage = '';
  successMessage = '';

  // Listas para os dropdowns
  listaEspecies: string[] = [];
  listaPortes: string[] = [];
  listaSexos: string[] = [];
  listaClientes: ClienteResponse[] = [];

  constructor(private fb: FormBuilder, private animalService: AnimalService) {
    this.animalForm = this.fb.group({
      nome: ['', Validators.required],
      especie: [null, Validators.required],
      porte: [null, Validators.required],
      raca: ['', Validators.required],
      sexo: [null, Validators.required],
      cor: [''],
      peso: [null, [Validators.required, Validators.min(0)]],
      castrado: [false, Validators.required],
      dataNascimento: ['', Validators.required],
      observacao: [''],
      clienteId: [null, Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadAnimais();
    this.loadDropdownData();
  }

  loadDropdownData(): void {
    this.animalService.getEspecies().subscribe(data => this.listaEspecies = data);
    this.animalService.getPortes().subscribe(data => this.listaPortes = data);
    this.animalService.getSexos().subscribe(data => this.listaSexos = data);
    this.animalService.getClientes().subscribe(data => this.listaClientes = data);
  }

  loadAnimais(): void {
    this.animalService.getAnimais().subscribe(data => this.animais = data);
  }

  onSubmit(): void {
    if (this.animalForm.invalid) {
      this.errorMessage = 'Formulário inválido. Verifique todos os campos.';
      return;
    }

    const animalData = this.animalForm.value;
    if (this.isEditing && this.currentAnimalId) {
      this.animalService.updateAnimal(this.currentAnimalId, animalData).subscribe({
        next: () => {
          this.successMessage = 'Animal atualizado com sucesso!';
          this.resetForm();
        },
        error: (err) => this.errorMessage = 'Erro ao atualizar animal.'
      });
    } else {
      this.animalService.createAnimal(animalData).subscribe({
        next: () => {
          this.successMessage = 'Animal cadastrado com sucesso!';
          this.resetForm();
        },
        error: (err) => this.errorMessage = 'Erro ao cadastrar animal.'
      });
    }
  }

  editAnimal(animal: AnimalResponse): void {
    this.isEditing = true;
    this.currentAnimalId = animal.id;
    this.animalForm.patchValue({
      ...animal,
      clienteId: animal.cliente.id,
      dataNascimento: animal.dataNascimento.split('T')[0]
    });
  }

  deleteAnimal(id: number): void {
    if (confirm('Tem certeza que deseja excluir este animal?')) {
      this.animalService.deleteAnimal(id).subscribe(() => this.loadAnimais());
    }
  }

  resetForm(): void {
    this.animalForm.reset({ castrado: false });
    this.isEditing = false;
    this.currentAnimalId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.loadAnimais();
  }
}
