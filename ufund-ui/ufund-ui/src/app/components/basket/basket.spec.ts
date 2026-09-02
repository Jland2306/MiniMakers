// Unit test for BasketComponent
// Ensures that the component initializes correctly

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BasketComponent } from './basket';

describe('Basket', () => {
  let component: BasketComponent;
  let fixture: ComponentFixture<BasketComponent>;

  beforeEach(async () => {
     // Configure testing module and import standalone BasketComponent
    await TestBed.configureTestingModule({
      imports: [BasketComponent],
    }).compileComponents();

    // Create component instance
    fixture = TestBed.createComponent(BasketComponent);
    component = fixture.componentInstance;
    
    // Wait for async initialization
    await fixture.whenStable();
  });

  // Basic test to verify component is created successfully
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
