//Handles user authentication for both Helpers and Managers.
//Determines user role and redirects to the appropriate page.

import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
 // Stores user input for login form
  username: string = '';
  password: string = '';

  // Stores user input for login form
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}
 
  // Displays error message if login fails
  login(): void {
    const success = this.authService.login(this.username, this.password);
    if (!success) {
      this.errorMessage = "Invalid username or password";
      return;
    }
    // Determine user role after successful login
    const role = this.authService.getRole();

    // Redirect based on role
    if (role === 'manager') {
      this.router.navigate(['/manager-dashboard']);
    } else {
      this.router.navigate(['/needs']);
    }
  }
}