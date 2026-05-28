import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestaoFuncionarioComponent } from './gestao-funcionario.component';

describe('GestaoFuncionarioComponent', () => {
  let component: GestaoFuncionarioComponent;
  let fixture: ComponentFixture<GestaoFuncionarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestaoFuncionarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestaoFuncionarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
