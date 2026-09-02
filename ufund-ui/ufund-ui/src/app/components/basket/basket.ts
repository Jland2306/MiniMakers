// BasketComponent
// Handles all logic for the helper's funding basket
// Allows users to view, remove, and checkout needs

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BasketService } from '../../services/basket';
import { Need } from '../../models/need';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-basket',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './basket.html',
  styleUrls: ['./basket.css']
})
export class BasketComponent implements OnInit {
  // Stores all needs currently in the basket
  basket: Need[] = [];

  // Stores error messages to display in UI
  errorMessage: string = '';

  // Stores success messages (e.g., after checkout)
  successMessage: string = '';

  constructor(private basketService: BasketService, private cdr: ChangeDetectorRef) {}

  // Stores success messages (e.g., after checkout)
  ngOnInit(): void {
    this.loadBasket();
  }
   // Runs when component initializes
  loadBasket(): void {
    this.basketService.getBasket().subscribe({
      next: data => {console.log("BASKET DATA: ", data); 
        this.basket = data;
        this.cdr.detectChanges(); },
      error: err => this.errorMessage = 'Failed to load basket.'
    });
  }
  // Retrieves basket data from backend
  removeItem(need: Need): void {
    this.basketService.removeNeed(need.name).subscribe({
      next: () => this.loadBasket(), // Removes a specific need from the basket
      error: () => this.errorMessage = 'Failed to remove item.'
    });
  }

  // Get the total cost of the items in the basket
  getTotalCost(): number {
    return this.basket.reduce((total, item) => {
      return total + (item.cost * item.quantity);
    }, 0);
  }

  // Handles checkout process for all items in basket
  checkout(): void {
    this.basketService.checkout().subscribe({
      next: res => {
        this.successMessage = 'Checkout successful!';
        this.basket = [];
        //this.loadBasket();
      },
      error: err => {
        // Handles checkout process for all items in basket
        if (err.status === 409) {
          this.errorMessage = 'Some items exceed cupboard stock.';
        } else if (err.status === 410) {
          this.errorMessage = 'Some items are no longer in the cupboard.';
        } else {
          this.errorMessage = 'Checkout failed.';
        }
      }
    });
  }
}
