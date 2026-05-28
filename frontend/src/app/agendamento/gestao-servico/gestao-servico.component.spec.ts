import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { GestaoServicoComponent } from './gestao-servico.component';

describe('GestaoServicoComponent', () => {
  let component: GestaoServicoComponent;
  let fixture: ComponentFixture<GestaoServicoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // Importamos os módulos que o componente depende para que o teste funcione
      imports: [
        GestaoServicoComponent,
        HttpClientModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GestaoServicoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Este é o teste básico: ele apenas confirma se o componente foi criado com sucesso.
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
