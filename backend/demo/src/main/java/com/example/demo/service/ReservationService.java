
package com.example.demo.service;

import com.example.demo.dto.ReservationRequest;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}
