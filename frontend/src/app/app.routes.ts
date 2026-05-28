

import { Routes } from '@angular/router';
import { MenuNovoComponent } from './menu-novo/menu-novo.component';
import { MenuAdminComponent } from './menu-admin/menu-admin.component';
import { LoginComponent } from './login/login.component';
import { adminGuard, clienteGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'api/login', pathMatch: 'full' },
  { path: 'api/menu', component: MenuNovoComponent, canActivate: [clienteGuard] },
  { path: 'api/admin', component: MenuAdminComponent, canActivate: [adminGuard] },
  { path: 'api/login', component: LoginComponent },
  { path: '**', redirectTo: 'api/login' }
];
