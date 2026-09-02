// Handles all logic for displaying and managing Needs
// Includes functionality for helpers (view + basket)
// and managers (add, edit, delete)

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NeedService } from '../../services/need';
import { Need } from '../../models/need';
import { AuthService } from '../../services/auth';
import { BasketService } from '../../services/basket';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-need-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './need-list.html',
  styleUrls:['./need-list.css']
})
export class NeedListComponent implements OnInit {
 // Stores all needs retrieved from backend
  needs: Need[] = [];

  // Stores all needs retrieved from backend
  selectedNeed?: Need;

  // Search input value
  searchQuery: string= '';

  // Search input value
  newNeed: Need = {
  name: '',
  cost: null as any,
  quantity: null as any,
  type: ''
  };

  // Controls visibility of add/edit form
  showAddForm: boolean = false;

  // Tracks whether we are editing or adding
  editing: boolean = false;

  // Message for when needs are added to basket
  basketMessage: string = '';

  constructor(
  private needService: NeedService,
  private basketService: BasketService,
  private cdr: ChangeDetectorRef,
  public authService: AuthService) {}

  // Runs when component loads
  ngOnInit(): void {
    this.loadNeeds();
  }

  // Fetch all needs from backend
  loadNeeds(): void {
    this.needService.getAllNeeds().subscribe(data => {
      this.needs = data;
      this.cdr.detectChanges(); // ensures UI updates
    });
  } 

  // Search for needs based on user input
  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.loadNeeds();
      return;
    }
    this.needService.searchNeeds(this.searchQuery).subscribe(data => {
      this.needs = data;
    });
  }

  // Select a need to display details
  selectNeed(need: Need): void {
    this.selectedNeed = need;
  }

  // Delete a need (manager only)
  deleteNeed(name: string): void {
    this.needService.deleteNeed(name).subscribe(() => {
      this.loadNeeds();
    });
  }

  // Populate form with existing need data for editing
  startEdit(need: Need | undefined): void {
  if (!need) return;
  this.newNeed = {
    name: need.name,
    cost: need.cost,
    quantity: need.quantity,
    type: need.type
  };

  this.showAddForm = true;
  this.editing = true;
}

  // Handles both adding and updating a need
  addNeed(): void {
    if (!this.newNeed.name) {
      alert("Name is required");
      return;
  }
  if (this.editing) {
    // Update existing need
    this.needService.updateNeed(this.newNeed).subscribe(() => {
      this.loadNeeds();
      this.resetForm();
    });

  } else {
    // Create new need
    this.needService.createNeed(this.newNeed).subscribe(() => {
      this.loadNeeds();
      this.resetForm();
    });

  }
}

 // Reset form after add/edit
 resetForm(): void {
    this.newNeed = {
      name: '',
      cost: null as any,
      quantity: null as any,
      type: ''
    };
      this.showAddForm = false;
}
  // Reset form after add/edit
  addToBasket(need: Need | undefined): void {
  if (!need) return;
  this.basketService.addNeed(need).subscribe({
    next: () => {
      // alert("Added to basket!");
      this.basketMessage = 'Added to basket!';
      this.cdr.detectChanges();
      setTimeout(() => this.basketMessage = '', 500);
      this.cdr.detectChanges();
    },
    error: () => {
      // alert("Error adding to basket");
      this.basketMessage = 'Error adding to basket';
      this.cdr.detectChanges();
      setTimeout(() => this.basketMessage = '', 500);
      this.cdr.detectChanges();
    }
  });

}
}


