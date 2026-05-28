import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../services/session.service';

export const authGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  
  if (sessionService.isLoggedIn()) {
    return true;
  }
  
  router.navigate(['/api/login']);
  return false;
};

export const adminGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  const role = sessionService.getRole();
  
  if (sessionService.isLoggedIn() && (role === 'ROLE_ADMIN' || role === 'ROLE_VETERINARIO')) {
    return true;
  }
  
  if (sessionService.isLoggedIn()) {
    router.navigate(['/api/menu']);
  } else {
    router.navigate(['/api/login']);
  }
  return false;
};

export const clienteGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  const role = sessionService.getRole();
  
  if (sessionService.isLoggedIn() && role === 'ROLE_CLIENTE') {
    return true;
  }
  
  if (sessionService.isLoggedIn()) {
    router.navigate(['/api/admin']);
  } else {
    router.navigate(['/api/login']);
  }
  return false;
};
