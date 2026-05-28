import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestaoClienteComponent } from './gestao-cliente.component';

describe('GestaoClienteComponent', () => {
  let component: GestaoClienteComponent;
  let fixture: ComponentFixture<GestaoClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestaoClienteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestaoClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
