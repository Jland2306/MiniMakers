 //Defines the structure of a user in the application.
 // Used for authentication and role-based access control.

export interface User {
  // Unique username for login
  username: string;

  // Unique username for login
  password: string;

  // Role determines permissions in the system
  role: 'helper' | 'manager';
}