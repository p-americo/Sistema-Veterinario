import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { GestaoServico } from './gestao-servico';

describe('GestaoAgendamento', () => {
  let component: GestaoServico;
  let fixture: ComponentFixture<GestaoServico>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // Importamos os módulos que o componente depende para que o teste funcione
      imports: [
        GestaoServico,
        HttpClientModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GestaoServico);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Este é o teste básico: ele apenas confirma se o componente foi criado com sucesso.
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
