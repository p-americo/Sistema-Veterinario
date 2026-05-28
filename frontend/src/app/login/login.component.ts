import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { LoginCredentials } from '../models/auth.model';
import { StorageService } from '../services/storage.service';

@Component({
  selector: 'app-login',
  standalone: true,

  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  tipoUsuarioSelecionado: 'cliente' | 'admin' = 'cliente';
  login: string = '';
  senha: string = '';
  errorMessage: string = '';

  private router = inject(Router);
  private authService = inject(AuthService);
  private storageService = inject(StorageService);

  selecionarTipoUsuario(tipo: 'cliente' | 'admin'): void {
    this.tipoUsuarioSelecionado = tipo;
  }

  fazerLogin(): void {
    this.errorMessage = '';
    const payload: LoginCredentials = {
      login: this.login,
      senha: this.senha
    };

    if (!payload.login || !payload.senha) {
      this.errorMessage = 'Informe login e senha.';
      return;
    }

    this.authService.login(payload).subscribe({
      next: (response) => {
        // Salva o token para o interceptor usar
        this.storageService.setItem('authToken', response.token);
        // Redireciona após login
        if (this.tipoUsuarioSelecionado === 'admin') {
          this.router.navigate(['/api/admin']);
        } else {
          this.router.navigate(['/api/menu']);
        }
      },
      error: () => {
        this.errorMessage = 'Login ou senha inválidos. Tente novamente.';
      }
    });
  }
}
