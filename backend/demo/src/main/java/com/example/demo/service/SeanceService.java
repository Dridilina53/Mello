package com.example.demo.service;

import com.example.demo.dto.SeanceRequest;
import com.example.demo.entity.Seance;
import com.example.demo.entity.User;
import com.example.demo.repository.SeanceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ReservationRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class SeanceService {

    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final JdbcTemplate jdbcTemplate;

    public SeanceService(SeanceRepository seanceRepository,
                         UserRepository userRepository,
                         ReservationRepository reservationRepository,
                         JdbcTemplate jdbcTemplate) {
        this.seanceRepository = seanceRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Seance ajouterSeance(SeanceRequest request) {

    Integer coachId = request.getCoachId();

    User coach = userRepository.findById(coachId)
            .orElseThrow(() -> new RuntimeException("Coach introuvable"));

    if (!coach.getRole().equalsIgnoreCase("COACH")) {
        throw new RuntimeException("Ce compte n'est pas un coach");
    }

    String specialite = jdbcTemplate.query(
        "SELECT specialite FROM coach WHERE id = ?",
        rs -> rs.next() ? rs.getString("specialite") : null,
        coachId
    );

    if (specialite == null || specialite.trim().isEmpty()) {
        throw new RuntimeException("Aucune spécialité définie pour ce coach.");
    }

    Integer idSpecialite = jdbcTemplate.query(
        "SELECT id_specialite FROM specialites WHERE LOWER(TRIM(nom_specialite)) = LOWER(TRIM(?))",
        rs -> rs.next() ? rs.getInt("id_specialite") : null,
        specialite
    );

    if (idSpecialite == null) {
        throw new RuntimeException(
            "Spécialité introuvable dans la table specialites : " + specialite
        );
    }

    Seance seance = new Seance();

    seance.setCoachId(coachId);
    seance.setIdSpecialite(idSpecialite);
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
    
public java.util.List<java.util.Map<String, Object>> getSeancesDisponibles() {

    String sql =
        "SELECT " +
        "s.id_seance AS idSeance, " +
        "s.coach_id AS coachId, " +
        "CONCAT(u.prenom, ' ', u.nom) AS coachNom, " +
        "sp.nom_specialite AS specialite, " +
        "s.titre AS titre, " +
        "s.description AS description, " +
        "s.date_seance AS dateSeance, " +
        "s.heure_debut AS heureDebut, " +
        "s.heure_fin AS heureFin, " +
        "TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin) AS dureeMinutes, " +
        "s.prix AS prix " +
        "FROM seances s " +
        "JOIN users u ON u.id = s.coach_id " +
        "JOIN specialites sp ON sp.id_specialite = s.id_specialite " +
        "WHERE UPPER(TRIM(s.statut)) = 'DISPONIBLE' " +
        "ORDER BY s.date_seance, s.heure_debut";

    return jdbcTemplate.queryForList(sql);
}

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