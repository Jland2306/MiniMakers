// Handles user registration for new helpers.
//Allows users to create an account and then redirects to login.

import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
// Stores user input from registration form
  username = '';
  password = '';

  // Displays error message if registration fails
  errorMessage = '';

  constructor(private auth: AuthService, private router: Router) {}
  
  // Handles registration form submission
  register() {
    const success = this.auth.register(this.username, this.password);

    // If username already exists, show error
    if (!success) {
      this.errorMessage = "Username already exists";
      return;
    }
    
    // Redirect to login page after successful registration
    this.router.navigate(['/login']);
  }
}