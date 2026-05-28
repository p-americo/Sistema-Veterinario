import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-animal-novo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './animal-novo.component.html',
  styleUrls: ['./animal-novo.component.css']
})
export class AnimalNovoComponent implements OnInit {
  @Input() clienteId!: number;
  @Input() listaEspecies: string[] = [];
  @Input() listaPortes: string[] = [];
  @Input() listaSexos: string[] = [];
  @Input() isLoading = false;
  @Input() errorMessage: string | null = null;
  @Input() successMessage: string | null = null;

  @Output() salvar = new EventEmitter<FormData>();
  @Output() cancelar = new EventEmitter<void>();

  animalForm: FormGroup;
  imagemSelecionada: File | null = null;
  imagemPreview: string | ArrayBuffer | null = null;

  constructor(private fb: FormBuilder) {
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
    if (this.animalForm.invalid || this.isLoading) {
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

    this.salvar.emit(formData);
  }

  onCancel(): void {
    this.cancelar.emit();
  }

  resetForm(): void {
    this.animalForm.reset({ castrado: false, clienteId: this.clienteId });
    this.imagemPreview = null;
    this.imagemSelecionada = null;
  }
}
