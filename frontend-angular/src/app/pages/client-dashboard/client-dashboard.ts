
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './client-dashboard.html',
  styleUrl: './client-dashboard.css'
})
export class ClientDashboard {

  recherche_text = '';
  selectedSession: any = null;

  sessions = [
    {
      coach: 'Sarah Martin',
      specialty: 'Gestion du stress',
      date: '20 Novembre 2026',
      time: '10:00',
      duration: '60 min',
      price: '45 DT',
      initials: 'SM'
    },
    {
      coach: 'Amine Ben Ali',
      specialty: 'Développement personnel',
      date: '21 Novembre 2026',
      time: '14:00',
      duration: '45 min',
      price: '35 DT',
      initials: 'AB'
    },
    {
      coach: 'Emma Wilson',
      specialty: 'Confiance en soi',
      date: '22 Novembre 2026',
      time: '11:30',
      duration: '60 min',
      price: '40 DT',
      initials: 'EW'
    },
    {
      coach: 'Youssef Karim',
      specialty: 'Coaching professionnel',
      date: '24 Novembre 2026',
      time: '16:00',
      duration: '60 min',
      price: '50 DT',
      initials: 'YK'
    }
  ];

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
  alert('Votre demande de réservation a été envoyée !');

  this.selectedSession = null;
}
}
