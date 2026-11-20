
package com.example.demo.service;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.Seance;
import com.example.demo.entity.User;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.SeanceRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeanceRepository seanceRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            SeanceRepository seanceRepository) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.seanceRepository = seanceRepository;
    }

    @Transactional
    public Reservation creerReservation(
            Integer clientId,
            Integer seanceId,
            String message) {

        // Find the client
        User client = userRepository.findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException("Client introuvable"));

        // Verify client role
        if (!"CLIENT".equalsIgnoreCase(client.getRole())) {
            throw new RuntimeException(
                    "Seul un client peut réserver une séance");
        }

        // Find the session
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() ->
                        new RuntimeException("Séance introuvable"));

        // Verify session availability
        if (!"DISPONIBLE".equalsIgnoreCase(seance.getStatut())) {
            throw new RuntimeException(
                    "Cette séance n'est pas disponible");
        }

        // Create reservation
        Reservation reservation = new Reservation();

        reservation.setClient(client);
        reservation.setSeance(seance);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setMessage(message);
        reservation.setStatut("EN_ATTENTE");

        return reservationRepository.save(reservation);
    }
}
