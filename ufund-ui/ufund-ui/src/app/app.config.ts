// Sets up global providers for the Angular application,
// including routing and error handling.

import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    // Enables global error handling for the application
    provideBrowserGlobalErrorListeners(),
    
    // Registers application routes (navigation between components)
    provideRouter(routes)
  ]
};
