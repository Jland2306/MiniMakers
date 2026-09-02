//Represents a need item retrieved from the backend cupboard.
//Used throughout the application for displaying and managing needs.

export interface Need {
  // Name of the need (acts as unique identifier)
  name: string;

  // Cost per unit of the need
  cost: number;

  // Number of units available
  quantity: number;

   // Category or type of the need
  type: string;
}

