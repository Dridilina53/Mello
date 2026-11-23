
package com.example.demo.controller;

import com.example.demo.entity.Seance;
import com.example.demo.repository.SeanceRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seances")
@CrossOrigin(origins = "http://localhost:4200")
public class SeanceController {

    private final SeanceRepository seanceRepository;

    public SeanceController(SeanceRepository seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    // Get all available sessions
    @GetMapping
    public ResponseEntity<List<Seance>> getAvailableSeances() {

        List<Seance> seances =
                seanceRepository.findByStatutIgnoreCase("DISPONIBLE");

        return ResponseEntity.ok(seances);
    }

    // Get one session by ID
    @GetMapping("/{id}")
    public ResponseEntity<Seance> getSeanceById(
            @PathVariable Integer id) {

        return seanceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
