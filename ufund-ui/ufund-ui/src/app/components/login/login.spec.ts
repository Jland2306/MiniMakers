// Unit test for LoginComponent
// Ensures that the component initializes correctly

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login';

describe('Login', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    // Configure testing module and import standalone LoginComponent
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
    }).compileComponents();

    // Create component instance
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    // Wait for async initialization
    await fixture.whenStable();
  });

  // Basic test to verify component is created successfully
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});