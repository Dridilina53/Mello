package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationResponse {

    private Integer idReservation;
    private String clientNom;
    private String titreSeance;
    private String coachNom;
    private String specialite;
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private Integer dureeMinutes;
    private BigDecimal prix;
    private String message;
    private String statut;

    public ReservationResponse(Integer idReservation, String clientNom,
            String titreSeance, String coachNom, String specialite,
            LocalDate dateSeance, LocalTime heureDebut,
            Integer dureeMinutes, BigDecimal prix,
            String message, String statut) {

        this.idReservation = idReservation;
        this.clientNom = clientNom;
        this.titreSeance = titreSeance;
        this.coachNom = coachNom;
        this.specialite = specialite;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.dureeMinutes = dureeMinutes;
        this.prix = prix;
        this.message = message;
        this.statut = statut;
    }

    public Integer getIdReservation() { return idReservation; }
    public String getClientNom() { return clientNom; }
    public String getTitreSeance() { return titreSeance; }
    public String getCoachNom() { return coachNom; }
    public String getSpecialite() { return specialite; }
    public LocalDate getDateSeance() { return dateSeance; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public BigDecimal getPrix() { return prix; }
    public String getMessage() { return message; }
    public String getStatut() { return statut; }
}
