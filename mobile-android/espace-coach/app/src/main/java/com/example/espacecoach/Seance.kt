package com.example.espacecoach

/** Une séance créée par un coach. */
data class Seance(
    val id: Int,
    val coachEmail: String,   // à quel coach appartient la séance
    val coachNom: String,
    val titre: String,
    val specialite: String,
    val date: String,         // ex : 25/11/2026
    val heure: String,        // ex : 14:30
    val dureeMinutes: Int,
    val prix: Int,            // en DT
    val mode: String          // "Visio" ou "Présentiel"
)