import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AnimalService } from '../services/animal.service';

@Component({
  selector: 'app-animal-novo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './animal-novo.component.html',
  styleUrls: ['./animal-novo.component.css']
})
export class AnimalNovoComponent implements OnInit {
  @Input() clienteId!: number;
  @Output() petCadastrado = new EventEmitter<void>();

  animalForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  imagemSelecionada: File | null = null;
  imagemPreview: string | ArrayBuffer | null = null;


  listaEspecies: string[] = [];
  listaPortes: string[] = [];
  listaSexos: string[] = [];

  constructor(private fb: FormBuilder, private animalService: AnimalService) {
    this.animalForm = this.fb.group({
      nome: ['', Validators.required],
      especie: [null, Validators.required],
      porte: [null, Validators.required],
      raca: ['', Validators.required],
      sexo: [null, Validators.required],
      peso: [null, [Validators.required, Validators.min(0.1)]],
      castrado: [false, Validators.required],
      dataNascimento: ['', Validators.required],
      observacao: [''],
      clienteId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.clienteId) {
      this.animalForm.patchValue({ clienteId: this.clienteId });
    }
    this.loadDropdownData();
  }

  loadDropdownData(): void {
    this.animalService.getEspecies().subscribe(data => this.listaEspecies = data);
    this.animalService.getPortes().subscribe(data => this.listaPortes = data);
    this.animalService.getSexos().subscribe(data => this.listaSexos = data);
  }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.imagemSelecionada = file;

      const reader = new FileReader();
      reader.onload = () => {
        this.imagemPreview = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }


  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.animalForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }
    if (!this.imagemSelecionada) {
      this.errorMessage = 'Por favor, adicione uma foto do pet.';
      return;
    }


    const formValues = this.animalForm.value;

    const animalDataParaJson = {
      nome: formValues.nome,
      especie: formValues.especie,
      porte: formValues.porte,
      raca: formValues.raca,
      sexo: formValues.sexo,
      peso: formValues.peso,
      castrado: formValues.castrado,
      dataNascimento: formValues.dataNascimento,
      observacao: formValues.observacao,
      clienteId: formValues.clienteId
    };

    const formData = new FormData();

    formData.append('dados', new Blob([JSON.stringify(animalDataParaJson)], { type: 'application/json' }));


    formData.append('imagem', this.imagemSelecionada, this.imagemSelecionada.name);


    this.animalService.createAnimal(formData).subscribe({
      next: () => {
        this.successMessage = 'Pet cadastrado com sucesso!';
        this.animalForm.reset({ castrado: false, clienteId: this.clienteId });
        this.imagemPreview = null;
        this.imagemSelecionada = null;
        setTimeout(() => this.petCadastrado.emit(), 1500);
      },
      error: (err) => {
        this.errorMessage = 'Erro ao cadastrar o pet. Verifique os dados e tente novamente.';
        console.error(err);
      }
    });
  }
}
