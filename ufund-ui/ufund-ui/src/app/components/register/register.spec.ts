// Unit test for RegisterComponent
// Ensures that the component initializes correctly

import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  RegisterComponent } from './register';

describe('Register', () => {
  let component:  RegisterComponent;
  let fixture: ComponentFixture< RegisterComponent>;

  beforeEach(async () => {
    // Configure testing module and import standalone RegisterComponent
    await TestBed.configureTestingModule({
      imports: [ RegisterComponent],
    }).compileComponents();
    
    // Create component instance
    fixture = TestBed.createComponent( RegisterComponent);
    component = fixture.componentInstance;

    // Wait for async initialization
    await fixture.whenStable();
  });
  
  // Basic test to verify component is created successfully
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
