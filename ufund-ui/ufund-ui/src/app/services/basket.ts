// Handles all HTTP requests related to the funding basket.
// Connects the Angular frontend to the backend Basket API.

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Need } from '../models/need';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BasketService {

  private apiUrl = 'http://localhost:8080/basket';
  constructor(private http: HttpClient) {}

  //Adds a need to the basket
  addNeed(need: Need): Observable<Need> {
    return this.http.post<Need>(this.apiUrl, {
      name: need.name, quantity: 1 });
  }

  //Retrieves all items currently in the basket
  getBasket(): Observable<Need[]> {
    return this.http.get<Need[]>(this.apiUrl);
  }

  //Removes a specific need from the basket by name
  removeNeed(name: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${name}`);
  }

  //Performs checkout of all basket items
  checkout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/checkout`, {});
  }
}