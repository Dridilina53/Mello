
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReservationData {
  clientId: number;
  titreSeance: string;
  coachNom: string;
  specialite: string;
  dateSeance: string;
  heureDebut: string;
  dureeMinutes: number;
  prix: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/reservations';

  createReservation(data: ReservationData): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }
}
