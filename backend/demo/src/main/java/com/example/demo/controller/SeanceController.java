package com.example.demo.controller;

import com.example.demo.entity.Seance;
import com.example.demo.dto.SeanceRequest;
import com.example.demo.repository.SeanceRepository;
import com.example.demo.service.SeanceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seances")
@CrossOrigin(origins = "http://localhost:4200")
public class SeanceController {

    private final SeanceRepository seanceRepository;
    private final SeanceService seanceService;

    public SeanceController(SeanceRepository seanceRepository,
                            SeanceService seanceService) {
        this.seanceRepository = seanceRepository;
        this.seanceService = seanceService;
    }

    
@GetMapping
public List<java.util.Map<String, Object>> getAvailableSeances() {
    return seanceService.getSeancesDisponibles();
}


    @GetMapping("/{id}")
    public ResponseEntity<Seance> getSeanceById(@PathVariable Integer id) {
        return seanceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/coach/{coachId}")
    public List<Seance> getSeancesByCoach(@PathVariable Integer coachId) {
        return seanceRepository.findByCoachId(coachId);
    }

    @PostMapping
    public Seance ajouterSeance(@RequestBody SeanceRequest request) {
        return seanceService.ajouterSeance(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seance> modifierSeance(
            @PathVariable Integer id,
            @RequestParam Integer coachId,
            @RequestBody SeanceRequest request) {

        return ResponseEntity.ok(
                seanceService.modifierSeance(id, coachId, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSeance(
            @PathVariable Integer id,
            @RequestParam Integer coachId) {

        seanceService.supprimerSeance(id, coachId);

        return ResponseEntity.noContent().build();
    }
}