package com.example.demo.service;

import com.example.demo.dto.SeanceRequest;
import com.example.demo.entity.Seance;
import com.example.demo.entity.User;
import com.example.demo.repository.SeanceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ReservationRepository;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class SeanceService {

    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public SeanceService(SeanceRepository seanceRepository,
                         UserRepository userRepository,
                         ReservationRepository reservationRepository) {
        this.seanceRepository = seanceRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    // Ajouter une disponibilité
    public Seance ajouterSeance(SeanceRequest request) {

        User coach = userRepository.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (!coach.getRole().equalsIgnoreCase("COACH")) {
            throw new RuntimeException("Ce compte n'est pas un coach");
        }

        Seance seance = new Seance();

        seance.setCoachId(request.getCoachId());
        seance.setIdSpecialite(request.getIdSpecialite());
        seance.setTitre(request.getTitre());
        seance.setDescription(request.getDescription());
        seance.setDateSeance(request.getDateSeance());
        seance.setHeureDebut(request.getHeureDebut());

        LocalTime heureFin = request.getHeureDebut()
                .plusMinutes(request.getDureeMinutes());

        seance.setHeureFin(heureFin);
        seance.setPrix(request.getPrix());
        seance.setCapacite(1);
        seance.setStatut("DISPONIBLE");

        return seanceRepository.save(seance);
    }

    // Vérifier que la séance appartient au coach
    private Seance verifierProprietaire(Integer idSeance, Integer coachId) {

        Seance seance = seanceRepository.findById(idSeance)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        if (!seance.getCoachId().equals(coachId)) {
            throw new RuntimeException("Vous ne pouvez pas modifier cette séance");
        }

        return seance;
    }

    public Seance modifierSeance(Integer idSeance,
                                 Integer coachId,
                                 SeanceRequest request) {

        Seance seance = verifierProprietaire(idSeance, coachId);

        seance.setIdSpecialite(request.getIdSpecialite());
        seance.setTitre(request.getTitre());
        seance.setDescription(request.getDescription());
        seance.setDateSeance(request.getDateSeance());
        seance.setHeureDebut(request.getHeureDebut());

        LocalTime heureFin = request.getHeureDebut()
                .plusMinutes(request.getDureeMinutes());

        seance.setHeureFin(heureFin);
        seance.setPrix(request.getPrix());

        return seanceRepository.save(seance);
    }

    public void supprimerSeance(Integer idSeance, Integer coachId) {

        Seance seance = verifierProprietaire(idSeance, coachId);

        if (reservationRepository.existsBySeance_IdSeance(idSeance)) {
            throw new RuntimeException(
                    "Impossible de supprimer cette séance : elle possède déjà une réservation."
            );
        }

        seanceRepository.delete(seance);
    }
}