//Restricts access to manager-only routes.
//Allows access only if the logged-in user has the "manager" role.
//Redirects unauthorized users to the needs page.

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const managerGuard: CanActivateFn = () => {
  // Inject required services
  const authService = inject(AuthService);
  const router = inject(Router);

  // Allow access only if user role is "manager"
  if (authService.getRole() === 'manager') {
    return true;
  }

  // Redirect non-managers to needs page
  router.navigate(['/needs']);
  return false;
};