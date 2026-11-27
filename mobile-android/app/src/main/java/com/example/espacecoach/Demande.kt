package com.example.espacecoach

data class Demande(
    val id: Int,
    val clientNom: String,
    val seanceTitre: String,
    val date: String,
    val heure: String,
    val statut: String,
    val message: String
)