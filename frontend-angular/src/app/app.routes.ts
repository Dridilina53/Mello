import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { HomeComponent } from './pages/home/home';
import { CoachDashboard } from './pages/coach-dashboard/coach-dashboard';
import { ClientDashboard } from './pages/client-dashboard/client-dashboard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'register',
    component: Register
  },
  {
    path: 'client/dashboard',
    component: ClientDashboard
  },
  {
  path: 'coach/dashboard',
  component: CoachDashboard
 },
  {
    path: '**',
    redirectTo: ''
  }
];