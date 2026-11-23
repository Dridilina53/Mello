
package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Integer idReservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // Optional for now: we are not connecting the seances table yet.
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "seance_id", nullable = true)
    private Seance seance;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 20)
    private String statut;

    // Session information saved directly in the reservation
    @Column(name = "titre_seance", nullable = false, length = 150)
    private String titreSeance;

    @Column(name = "coach_nom", nullable = false, length = 150)
    private String coachNom;

    @Column(nullable = false, length = 150)
    private String specialite;

    @Column(name = "date_seance", nullable = false)
    private LocalDate dateSeance;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "duree_minutes", nullable = false)
    private Integer dureeMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    public Reservation() {
    }

    public Integer getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Integer idReservation) {
        this.idReservation = idReservation;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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
}
