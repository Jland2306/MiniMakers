// Connects to our api on the backend in order to get the 
// data required for manipulating needs

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Need } from '../models/need';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NeedService {

  private apiUrl = 'http://localhost:8080/cupboard';
  //private apiUrl = 'http://localhost:4200/';

  constructor(private http: HttpClient) {}

  //Retrieves all needs from the cupboard
  getAllNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.apiUrl);
  }

  //Retrieves a single need by name
  getNeed(name: string): Observable<Need> {
    return this.http.get<Need>(`${this.apiUrl}/${name}`);
  }

  //Creates a new need in the cupboard
  createNeed(need: Need): Observable<Need> {
    return this.http.post<Need>(this.apiUrl, need);
  }

  //Updates an existing need
  updateNeed(need: Need): Observable<Need> {
    return this.http.put<Need>(this.apiUrl, need);
  }

  //Deletes a need by name
  deleteNeed(name: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${name}`);
  }

  //Searches for needs by partial name match
  searchNeeds(query: string): Observable<Need[]> {
    return this.http.get<Need[]>(`${this.apiUrl}/search`, {
      params: new HttpParams().set('query', query)
    });
  }
}
