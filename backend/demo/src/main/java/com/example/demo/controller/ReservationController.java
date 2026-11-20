
package com.example.demo.controller;

import com.example.demo.dto.ReservationRequest;
import com.example.demo.entity.Reservation;
import com.example.demo.service.ReservationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> creerReservation(
            @RequestBody ReservationRequest request) {

        Reservation reservation = reservationService.creerReservation(
                request.getClientId(),
                request.getSeanceId(),
                request.getMessage()
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("message", "Réservation créée avec succès");
        response.put("idReservation", reservation.getIdReservation());
        response.put("clientId", reservation.getClient().getId());
        response.put("seanceId", reservation.getSeance().getIdSeance());
        response.put("statut", reservation.getStatut());
        response.put("dateReservation", reservation.getDateReservation());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
