package com.example.demo.dto;

public record RegisterRequest(
    String nom,
    String prenom,
    String email,
    String telephone,
    String dateNaissance,
    String role,
    String specialite,
    Integer experience,
    String diplome,
    String bio,
    String password
) {}