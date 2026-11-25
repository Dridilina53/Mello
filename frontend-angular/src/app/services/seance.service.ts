import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SeanceService {

  private http = inject(HttpClient);

  private url = 'http://localhost:8080/api/seances';

  ajouterSeance(seance: any) {
    return this.http.post(this.url, seance);
  }

  getSeancesCoach(coachId: number) {
    return this.http.get<any[]>(this.url + '/coach/' + coachId);
  }

  getSeancesDisponibles() {
    return this.http.get<any[]>(this.url);
  }

  modifierSeance(id: number, coachId: number, seance: any) {
    return this.http.put(
      this.url + '/' + id + '?coachId=' + coachId,
      seance
    );
  }

  supprimerSeance(id: number, coachId: number) {
    return this.http.delete(
      this.url + '/' + id + '?coachId=' + coachId
    );
  }
}