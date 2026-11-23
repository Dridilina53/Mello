
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationRequest {

    private Integer clientId;

    private String titreSeance;
    private String coachNom;
    private String specialite;

    private LocalDate dateSeance;
    private LocalTime heureDebut;

    private Integer dureeMinutes;
    private BigDecimal prix;

    private String message;

    public ReservationRequest() {
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getTitreSeance() {
        return titreSeance;
    }

    public void setTitreSeance(String titreSeance) {
        this.titreSeance = titreSeance;
    }

    public String getCoachNom() {
        return coachNom;
    }

    public void setCoachNom(String coachNom) {
        this.coachNom = coachNom;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Integer getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(Integer dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
