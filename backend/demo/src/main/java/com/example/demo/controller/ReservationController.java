package com.example.demo.controller;

import com.example.demo.dto.ReservationRequest;
import com.example.demo.dto.ReservationResponse;
import com.example.demo.entity.Reservation;
import com.example.demo.service.ReservationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestBody ReservationRequest request) {

        try {
            Reservation reservation =
                    reservationService.createReservation(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(reservation);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/coach")
    public ResponseEntity<?> getReservationsDuCoach(
            @RequestParam String coachNom) {

        try {
            List<ReservationResponse> liste =
                    reservationService.getReservationsDuCoach(coachNom);

            return ResponseEntity.ok(liste);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/accepter")
    public ResponseEntity<?> accepterReservation(
            @PathVariable Integer id,
            @RequestParam String coachNom) {

        try {
            ReservationResponse reponse =
                    reservationService.accepterReservation(id, coachNom);

            return ResponseEntity.ok(reponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/refuser")
    public ResponseEntity<?> refuserReservation(
            @PathVariable Integer id,
            @RequestParam String coachNom) {

        try {
            ReservationResponse reponse =
                    reservationService.refuserReservation(id, coachNom);

            return ResponseEntity.ok(reponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}