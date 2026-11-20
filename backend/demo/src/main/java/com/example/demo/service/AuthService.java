
package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;


import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate) {

        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Integer register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                "Cette adresse email est déjà utilisée."
            );
        }

        String role = request.role()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!role.equals("CLIENT") && !role.equals("COACH")) {
            throw new IllegalArgumentException("Rôle invalide.");
        }

        if (role.equals("COACH")) {
            if (request.specialite() == null
                    || request.specialite().isBlank()
                    || request.diplome() == null
                    || request.diplome().isBlank()) {
                throw new IllegalArgumentException(
                    "La spécialité et le diplôme sont obligatoires."
                );
            }
        }

        User user = new User();

        user.setNom(request.nom());
        user.setPrenom(request.prenom());
        user.setEmail(request.email());
        user.setPassword(
            passwordEncoder.encode(request.password())
        );
        user.setRole(role);
        user.setStatus("ACTIVE");
LocalDate dateNaissance = LocalDate.parse(request.dateNaissance());
user.setDateNaissance(dateNaissance);

user.setTelephone(request.telephone());
       user.setTelephone(request.telephone());

        User savedUser = userRepository.save(user);

        if (role.equals("CLIENT")) {

            jdbcTemplate.update(
                "INSERT INTO client (id) VALUES (?)",
                savedUser.getId()
            );

        } else {

            jdbcTemplate.update(
                """
                INSERT INTO coach
                (id, specialite, biographie, experience, diplome)
                VALUES (?, ?, ?, ?, ?)
                """,
                savedUser.getId(),
                request.specialite(),
                request.bio(),
                request.experience(),
                request.diplome()
            );
        }

        return savedUser.getId();
    }

public User login(LoginRequest request) {

    User user = userRepository.findByEmail(request.email())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Email ou mot de passe incorrect."
                )
            );

    boolean passwordCorrect = passwordEncoder.matches(
            request.password(),
            user.getPassword()
    );

    if (!passwordCorrect) {
        throw new IllegalArgumentException(
            "Email ou mot de passe incorrect."
        );
    }

    if (!"ACTIVE".equals(user.getStatus())) {
        throw new IllegalArgumentException(
            "Ce compte n'est pas actif."
        );
    }

    return user;
}

}
