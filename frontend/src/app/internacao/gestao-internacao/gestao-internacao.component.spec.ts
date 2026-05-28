import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { GestaoInternacaoComponent } from './gestao-internacao.component';

describe('GestaoInternacaoComponent', () => {
  let component: GestaoInternacaoComponent;
  let fixture: ComponentFixture<GestaoInternacaoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // Importamos os módulos que o componente depende para que o teste funcione
      imports: [
        GestaoInternacaoComponent,
        HttpClientModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GestaoInternacaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Este é o teste básico: ele apenas confirma se o componente foi criado com sucesso.
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
