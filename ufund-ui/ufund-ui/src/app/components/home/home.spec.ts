// Unit test for Home component
// Verifies that the home page component initializes correctly

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Home } from './home';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  beforeEach(async () => {
    // Configure testing module and import standalone Home component
    await TestBed.configureTestingModule({
      imports: [Home],
    }).compileComponents();

    // Create component instance
    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    
    // Wait for any async initialization
    await fixture.whenStable();
  });

  // Basic test to ensure component is created successfully
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
