//Protects routes from being accessed by unauthenticated users.
//Redirects users to the login page if they are not logged in.

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = () => {
   // Inject required services
  const authService = inject(AuthService);
  const router = inject(Router);

  // Allow access if user is logged in
  if (authService.isLoggedIn()) {
    return true;
  }
  
// Allow access if user is logged in
  router.navigate(['/login']);
  return false;
};