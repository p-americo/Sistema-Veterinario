import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private storageService = inject(StorageService);
  private router = inject(Router);

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      if (!payload) return null;
      let base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      while (base64.length % 4) {
        base64 += '=';
      }
      const decoded = atob(base64);
      return JSON.parse(decoded);
    } catch {
      return null;
    }
  }

  getRole(): string | null {
    const token = this.storageService.getItem('authToken');
    if (!token) return null;
    const parsed = this.decodeToken(token);
    return parsed ? parsed.role || null : null;
  }

  getUserCpf(): string | null {
    const token = this.storageService.getItem('authToken');
    if (!token) return null;
    const parsed = this.decodeToken(token);
    return parsed ? parsed.sub || null : null;
  }

  isLoggedIn(): boolean {
    return !!this.storageService.getItem('authToken');
  }

  logout(): void {
    this.storageService.removeItem('authToken');
    this.router.navigate(['/api/login']);
  }
}
