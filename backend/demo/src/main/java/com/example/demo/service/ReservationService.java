package com.example.demo.service;

import com.example.demo.dto.ReservationRequest;
import com.example.demo.dto.ReservationResponse;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request) {

        // 1. Find the client
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Client introuvable."));

        // 2. Verify that the account belongs to a client
        if (!"CLIENT".equalsIgnoreCase(client.getRole())) {
            throw new IllegalArgumentException(
                    "Seuls les clients peuvent effectuer une réservation.");
        }

        // 3. Create a new reservation
        Reservation reservation = new Reservation();

        reservation.setClient(client);

        // No seance relationship for now
        reservation.setSeance(null);

        // 4. Save session information
        reservation.setTitreSeance(request.getTitreSeance());
        reservation.setCoachNom(request.getCoachNom());
        reservation.setSpecialite(request.getSpecialite());
        reservation.setDateSeance(request.getDateSeance());
        reservation.setHeureDebut(request.getHeureDebut());
        reservation.setDureeMinutes(request.getDureeMinutes());
        reservation.setPrix(request.getPrix());

        // 5. Save reservation information
        reservation.setMessage(request.getMessage());
        reservation.setDateReservation(LocalDateTime.now());

        // 6. Set initial status
        reservation.setStatut("EN_ATTENTE");

        // 7. Save in MySQL
        return reservationRepository.save(reservation);
    }

    // GÉRER LES RÉSERVATIONS (côté coach) 

    // Les réservations adressées à ce coach. 
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsDuCoach(String coachNom) {
        verifierNomCoach(coachNom);

        return reservationRepository.findAll().stream()
                .filter(r -> r.getCoachNom().equalsIgnoreCase(coachNom.trim()))
                .map(this::versReponse)
                .collect(Collectors.toList());
    }

    // Le coach accepte une réservation : le statut passe à ACCEPTEE. 
    @Transactional
    public ReservationResponse accepterReservation(Integer id, String coachNom) {
        Reservation reservation = trouverPourCoach(id, coachNom);
        reservation.setStatut("ACCEPTEE");
        return versReponse(reservationRepository.save(reservation));
    }

    // Le coach supprime une réservation. 
    @Transactional
    public void supprimerReservation(Integer id, String coachNom) {
        Reservation reservation = trouverPourCoach(id, coachNom);
        reservationRepository.delete(reservation);
    }

    //  Méthodes privées 

    private void verifierNomCoach(String coachNom) {
        if (coachNom == null || coachNom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du coach est obligatoire.");
        }
    }

    // Cherche la réservation ET vérifie qu'elle est bien adressée à ce coach. 
    private Reservation trouverPourCoach(Integer id, String coachNom) {
        verifierNomCoach(coachNom);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Réservation introuvable."));

        if (!reservation.getCoachNom().equalsIgnoreCase(coachNom.trim())) {
            throw new IllegalArgumentException(
                    "Cette réservation n'est pas adressée à ce coach.");
        }
        return reservation;
    }

    //  Transforme l'entité en réponse sans mot de passe. 
    private ReservationResponse versReponse(Reservation r) {
        return new ReservationResponse(
                r.getIdReservation(),
                nomDuClient(r.getClient()),
                r.getTitreSeance(),
                r.getCoachNom(),
                r.getSpecialite(),
                r.getDateSeance(),
                r.getHeureDebut(),
                r.getDureeMinutes(),
                r.getPrix(),
                r.getMessage(),
                r.getStatut()
        );
    }

   
    private String nomDuClient(User client) {
        return client.getPrenom() + " " + client.getNom();
    }
}