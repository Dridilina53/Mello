
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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

export interface ReservationResponse {
  idReservation: number;
  clientNom: string;
  titreSeance: string;
  coachNom: string;
  specialite: string;
  dateSeance: string;
  heureDebut: string;
  dureeMinutes: number;
  prix: number;
  message: string;
  statut: string;
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
  getReservationsCoach(coachNom: string): Observable<ReservationResponse[]> {
    const params = new HttpParams().set('coachNom', coachNom);

    return this.http.get<ReservationResponse[]>(
      `${this.apiUrl}/coach`,
      { params }
    );
  }

  accepterReservation(id: number, coachNom: string): Observable<any> {
    const params = new HttpParams().set('coachNom', coachNom);

    return this.http.put<any>(
      `${this.apiUrl}/${id}/accepter`,
      {},
      { params }
    );
  }

  refuserReservation(id: number, coachNom: string): Observable<any> {
    const params = new HttpParams().set('coachNom', coachNom);

    return this.http.put<any>(
      `${this.apiUrl}/${id}/refuser`,
      {},
      { params }
    );
  }
}
