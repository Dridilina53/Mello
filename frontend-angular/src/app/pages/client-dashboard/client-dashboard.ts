
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReservationService } from '../../services/reservation.service';


@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './client-dashboard.html',
  styleUrl: './client-dashboard.css'
})
export class ClientDashboard {
  message = '';
  recherche_text = '';
  selectedSession: any = null;
  private reservationService = inject(ReservationService);
  sessions = [{
  coach: 'Sarah Martin',
  specialty: 'Gestion du stress',
  date: '20 Novembre 2026',
  dateIso: '2026-11-20',
  time: '10:00',
  duration: '60 min',
  durationMinutes: 60,
  price: '45 DT',
  priceValue: 45,
  initials: 'SM'
}];

  get filteredSessions() {
    const search = this.recherche_text.toLowerCase().trim();

    if (!search) return this.sessions;

    return this.sessions.filter(session =>
      session.coach.toLowerCase().includes(search) ||
      session.specialty.toLowerCase().includes(search)
    );
  }

  reserver(session: any) {
  this.selectedSession = session;
}
confirmerReservation() {
  const clientId = Number(localStorage.getItem('userId'));

  if (!clientId) {
    alert('Utilisateur non connecté.');
    return;
  }

  const session = this.selectedSession;

  const reservationData = {
    clientId: clientId,
    titreSeance: session.specialty,
    coachNom: session.coach,
    specialite: session.specialty,
    dateSeance: session.dateIso,
    heureDebut: session.time + ':00',
    dureeMinutes: session.durationMinutes,
    prix: session.priceValue,
    message: this.message
  };

  this.reservationService.createReservation(reservationData)
    .subscribe({
      next: () => {
        alert('Votre réservation a été enregistrée avec succès !');
        this.selectedSession = null;
        this.message = '';
      },
      error: (err) => {
        console.error('Reservation error:', err);
        alert('Erreur lors de la réservation.');
      }
    });
}
}
