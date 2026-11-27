package com.example.myapplication

data class SeanceClient(
    val idSeance: Int,
    val coachId: Int,
    val coachNom: String,
    val specialite: String,
    val titre: String,
    val description: String,
    val dateSeance: String,
    val heureDebut: String,
    val dureeMinutes: Int,
    val prix: Double
)