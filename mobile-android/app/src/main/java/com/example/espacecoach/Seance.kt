package com.example.espacecoach

data class Seance(
    val id: Int,
    val coachEmail: String,
    val coachNom: String,
    val titre: String,
    val specialite: String,
    val date: String,
    val heure: String,
    val dureeMinutes: Int,
    val prix: Int,
    val mode: String
)