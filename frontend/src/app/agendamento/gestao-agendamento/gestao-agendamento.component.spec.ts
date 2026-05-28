import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { GestaoAgendamentoComponent } from './gestao-agendamento.component';

describe('GestaoAgendamentoComponent', () => {
  let component: GestaoAgendamentoComponent;
  let fixture: ComponentFixture<GestaoAgendamentoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // Importamos os módulos que o componente depende para que o teste funcione
      imports: [
        GestaoAgendamentoComponent,
        HttpClientModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GestaoAgendamentoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Este é o teste básico: ele apenas confirma se o componente foi criado com sucesso.
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
