import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './layout/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent], // <-- Usa o nome correto
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  title = 'clinica-pet';
}
