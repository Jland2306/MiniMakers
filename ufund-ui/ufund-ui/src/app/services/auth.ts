//Handles user authentication, registration, and session management.
//Uses localStorage for persistence

import { Injectable } from '@angular/core';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
 //Stores currently logged-in user in memory
  private currentUser?: User;

  // Retrieves all registered users from localStorage
  private getUsers(): User[] {
    const stored = localStorage.getItem('users');
    return stored ? JSON.parse(stored) : [];
  }
 //Saves user list to localStorage
  private saveUsers(users: User[]) {
    localStorage.setItem('users', JSON.stringify(users));
  }
 // Registers a new helper user
  register(username: string, password: string): boolean {
    const users = this.getUsers();

    // Check if username already exists
    const exists = users.find(u => u.username === username);
    if (exists) return false;

  // Create new helper user
    const newUser: User = {
      username,
      password,
      role: 'helper'
    };
    users.push(newUser);
    this.saveUsers(users);

    return true;
  }
   // Admin login
  login(username: string, password: string): boolean {
    
    if (username === 'Admin' && password === '2026') {
      this.currentUser = { username, password, role: 'manager' };
      localStorage.setItem('user', JSON.stringify(this.currentUser));
      return true;
    }

    const users = this.getUsers();

   // Find matching user credentials
    const found = users.find(
      u => u.username === username && u.password === password
    );

    if (!found) return false;

    // Store logged-in user
    this.currentUser = found;
    localStorage.setItem('user', JSON.stringify(found));

    return true;
  }

   //Logs out the current use
  logout(): void {
    this.currentUser = undefined;
    localStorage.removeItem('user');
  }
 
  //Retrieves the currently logged-in user
  getUser(): User | null {
    if (this.currentUser) return this.currentUser;
    const stored = localStorage.getItem('user');
    if (!stored) return null;

    this.currentUser = JSON.parse(stored) as User;
    return this.currentUser;
  }

  //Checks if a user is currently logged in
  isLoggedIn(): boolean {
    return this.getUser() !== null;
  }

  //Returns the role of the current user
  getRole(): string | null {
    return this.getUser()?.role ?? null;
  }
}