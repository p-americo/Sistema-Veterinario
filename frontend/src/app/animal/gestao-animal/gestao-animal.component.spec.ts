import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { GestaoAnimalComponent } from './gestao-animal.component';

describe('GestaoAnimalComponent', () => {
  let component: GestaoAnimalComponent;
  let fixture: ComponentFixture<GestaoAnimalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // Importamos os módulos que o componente depende para que o teste funcione
      imports: [
        GestaoAnimalComponent,
        HttpClientModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GestaoAnimalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Este é o teste básico: ele apenas confirma se o componente foi criado com sucesso.
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
