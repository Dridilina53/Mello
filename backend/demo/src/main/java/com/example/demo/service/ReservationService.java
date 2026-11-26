
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

        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Client introuvable."));

        if (!"CLIENT".equalsIgnoreCase(client.getRole())) {
            throw new IllegalArgumentException(
                    "Seuls les clients peuvent effectuer une réservation.");
        }

        Reservation reservation = new Reservation();

        reservation.setClient(client);
        reservation.setSeance(null);

        reservation.setTitreSeance(request.getTitreSeance());
        reservation.setCoachNom(request.getCoachNom());
        reservation.setSpecialite(request.getSpecialite());
        reservation.setDateSeance(request.getDateSeance());
        reservation.setHeureDebut(request.getHeureDebut());
        reservation.setDureeMinutes(request.getDureeMinutes());
        reservation.setPrix(request.getPrix());

        reservation.setMessage(request.getMessage());
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut("EN_ATTENTE");

        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsDuCoach(String coachNom) {

        verifierNomCoach(coachNom);

        return reservationRepository.findAll().stream()
                .filter(r -> r.getCoachNom() != null
                        && r.getCoachNom().equalsIgnoreCase(coachNom.trim()))
                .map(this::versReponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse accepterReservation(Integer id, String coachNom) {

        Reservation reservation = trouverPourCoach(id, coachNom);

        if (!"EN_ATTENTE".equals(reservation.getStatut())) {
            throw new IllegalArgumentException(
                    "Cette demande n'est plus en attente.");
        }

        reservation.setStatut("CONFIRMEE");

        return versReponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse refuserReservation(Integer id, String coachNom) {

        Reservation reservation = trouverPourCoach(id, coachNom);

        if (!"EN_ATTENTE".equals(reservation.getStatut())) {
            throw new IllegalArgumentException(
                    "Cette demande n'est plus en attente.");
        }

        reservation.setStatut("ANNULEE");

        return versReponse(reservationRepository.save(reservation));
    }

    private void verifierNomCoach(String coachNom) {

        if (coachNom == null || coachNom.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Le nom du coach est obligatoire.");
        }
    }

    private Reservation trouverPourCoach(Integer id, String coachNom) {

        verifierNomCoach(coachNom);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Réservation introuvable."));

        if (reservation.getCoachNom() == null
                || !reservation.getCoachNom().equalsIgnoreCase(coachNom.trim())) {
            throw new IllegalArgumentException(
                    "Cette réservation n'est pas adressée à ce coach.");
        }

        return reservation;
    }

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
