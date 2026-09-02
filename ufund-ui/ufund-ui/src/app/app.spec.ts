// Unit test for App component
// Verifies that the root application component initializes correctly

import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    // Configure testing module and import standalone App component
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  // Test: App should be created successfully
  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  // Test: App should be created successfully
  it('should render title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Hello, ufund-ui');
  });
});
