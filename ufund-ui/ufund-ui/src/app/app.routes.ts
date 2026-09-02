// Deals with the website routing, this file decides
// which component needs to be shown on the webpage at what time

import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { NeedListComponent } from './component/need-list/need-list';
import { LoginComponent } from './components/login/login';
import { ManagerDashboardComponent } from './components/manager-dashboard/manager-dashboard';
import { authGuard } from './guards/auth.guard';
import { managerGuard } from './guards/role.guard';
import { RegisterComponent } from './components/register/register';
import { BasketComponent } from './components/basket/basket';

export const routes: Routes = [
  { path: '', component: Home}, // Default page
  { path: 'login', component: LoginComponent },
  { path: 'needs', component: NeedListComponent, canActivate: [authGuard] },
  { path: 'register', component: RegisterComponent },
  { path: 'manager-dashboard', component: ManagerDashboardComponent, canActivate: [authGuard, managerGuard] },
  { path: 'basket', component: BasketComponent, canActivate: [authGuard] },
];
