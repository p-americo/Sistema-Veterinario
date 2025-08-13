import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestaoCliente } from './gestao-cliente';

describe('GestaoCliente', () => {
  let component: GestaoCliente;
  let fixture: ComponentFixture<GestaoCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestaoCliente]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestaoCliente);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
