

import { Routes } from '@angular/router';
import { MenuNovoComponent } from './menu-novo/menu-novo.component';
import { MenuAdminComponent } from './menu-admin/menu-admin.component';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  { path: '', redirectTo: 'api/login', pathMatch: 'full' },
  { path: 'api/menu', component: MenuNovoComponent },
  { path: 'api/admin', component: MenuAdminComponent },
  { path: 'api/login', component: LoginComponent },
  { path: '**', redirectTo: 'api/login' }
];
