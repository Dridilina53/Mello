
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SeanceService } from '../../services/seance.service';
import {
  ReservationService,
  ReservationResponse
} from '../../services/reservation.service';

@Component({
  selector: 'app-coach-dashboard',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './coach-dashboard.html',
  styleUrl: './coach-dashboard.css'
})
export class CoachDashboard implements OnInit {

  private seanceService = inject(SeanceService);
  private reservationService = inject(ReservationService);

  activeSection = 'demandes';
  showForm = false;

  editingSessionId: number | null = null;

  coachId = Number(localStorage.getItem('userId'));
  coachNom = localStorage.getItem('coachNom') || '';

  reservations: ReservationResponse[] = [];
  sessions: any[] = [];

  newSession = {
    titre: '',
    specialite: 1,
    description: '',
    date: '',
    heure: '',
    duree: 60,
    prix: 0
  };

  ngOnInit() {
    this.chargerSeances();
    this.chargerReservations();
  }

  changeSection(section: string) {
    this.activeSection = section;

    if (section === 'demandes' || section === 'historique') {
      this.chargerReservations();
    }
  }


  chargerReservations() {
    if (!this.coachNom) {
      console.error('Nom du coach introuvable.');
      return;
    }

    this.reservationService.getReservationsCoach(this.coachNom)
      .subscribe({
        next: (data) => {
          this.reservations = data;
        },
        error: (err) => {
          console.error('Erreur chargement réservations :', err);
        }
      });
  }

  accepterReservation(id: number) {
    this.reservationService.accepterReservation(
      id,
      this.coachNom
    ).subscribe({
      next: () => {
        alert('Réservation acceptée avec succès !');
        this.chargerReservations();
      },
      error: (err) => {
        console.error('Erreur acceptation réservation :', err);
        alert(err.error?.message || 'Erreur lors de l’acceptation.');
      }
    });
  }

  refuserReservation(id: number) {
    const confirmation = confirm(
      'Voulez-vous vraiment refuser cette réservation ?'
    );

    if (!confirmation) return;

    this.reservationService.refuserReservation(
      id,
      this.coachNom
    ).subscribe({
      next: () => {
        alert('Réservation refusée.');
        this.chargerReservations();
      },
      error: (err) => {
        console.error('Erreur refus réservation :', err);
        alert(err.error?.message || 'Erreur lors du refus.');
      }
    });
  }


  chargerSeances() {
    this.seanceService.getSeancesCoach(this.coachId).subscribe({
      next: (data) => {
        this.sessions = data;
      },
      error: (err) => {
        console.error('Erreur chargement séances :', err);
      }
    });
  }

  ouvrirFormulaire() {
    this.editingSessionId = null;
    this.reinitialiserFormulaire();
    this.showForm = true;
  }

  annulerFormulaire() {
    this.showForm = false;
    this.editingSessionId = null;
    this.reinitialiserFormulaire();
  }

  reinitialiserFormulaire() {
    this.newSession = {
      titre: '',
      specialite: 1,
      description: '',
      date: '',
      heure: '',
      duree: 60,
      prix: 0
    };
  }

  ajouterSeance() {
    const seance = {
      coachId: this.coachId,
      idSpecialite: this.newSession.specialite,
      titre: this.newSession.titre,
      description: this.newSession.description,
      dateSeance: this.newSession.date,
      heureDebut: this.newSession.heure,
      dureeMinutes: this.newSession.duree,
      prix: this.newSession.prix
    };

    this.seanceService.ajouterSeance(seance).subscribe({
      next: () => {
        alert('Séance ajoutée avec succès !');
        this.chargerSeances();
        this.annulerFormulaire();
      },
      error: (err) => {
        console.error('Erreur ajout séance :', err);
        alert('Erreur lors de l’ajout de la séance.');
      }
    });
  }

  modifierSeance(session: any) {
    this.editingSessionId = session.idSeance;

    const debut = session.heureDebut.substring(0, 5);
    const fin = session.heureFin.substring(0, 5);

    const debutMinutes = Number(debut.substring(0, 2)) * 60
                       + Number(debut.substring(3, 5));

    const finMinutes = Number(fin.substring(0, 2)) * 60
                     + Number(fin.substring(3, 5));

    let duree = finMinutes - debutMinutes;

    if (duree <= 0) {
      duree += 24 * 60;
    }

    this.newSession = {
      titre: session.titre,
      specialite: session.idSpecialite,
      description: session.description || '',
      date: session.dateSeance,
      heure: debut,
      duree: duree,
      prix: session.prix
    };

    this.showForm = true;
  }

  enregistrerModification() {
    if (this.editingSessionId === null) return;

    const seance = {
      coachId: this.coachId,
      idSpecialite: this.newSession.specialite,
      titre: this.newSession.titre,
      description: this.newSession.description,
      dateSeance: this.newSession.date,
      heureDebut: this.newSession.heure,
      dureeMinutes: this.newSession.duree,
      prix: this.newSession.prix
    };

    this.seanceService.modifierSeance(
      this.editingSessionId,
      this.coachId,
      seance
    ).subscribe({
      next: () => {
        alert('Séance modifiée avec succès !');
        this.chargerSeances();
        this.annulerFormulaire();
      },
      error: (err) => {
        console.error('Erreur modification séance :', err);
        alert('Erreur lors de la modification de la séance.');
      }
    });
  }

  supprimerSeance(idSeance: number) {
    const confirmation = confirm(
      'Voulez-vous vraiment supprimer cette disponibilité ?'
    );

    if (!confirmation) return;

    this.seanceService.supprimerSeance(
      idSeance,
      this.coachId
    ).subscribe({
      next: () => {
        alert('Séance supprimée avec succès !');
        this.chargerSeances();
      },
      error: (err) => {
        console.error('Erreur suppression séance :', err);

        if (err.error?.message) {
          alert(err.error.message);
        } else {
          alert('Impossible de supprimer cette séance.');
        }
      }
    });
  }
}
