import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestaoFuncionario } from './gestao-funcionario';

describe('GestaoFuncionario', () => {
  let component: GestaoFuncionario;
  let fixture: ComponentFixture<GestaoFuncionario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestaoFuncionario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestaoFuncionario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
