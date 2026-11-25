package com.example.espacecoach

/** Une demande de réservation envoyée par un client au coach. */
data class Demande(
    val id: Int,
    val coachEmail: String,   // le coach qui reçoit la demande
    val clientNom: String,
    val seanceTitre: String,
    val date: String,
    val heure: String,
    val acceptee: Boolean = false // false = en attente, true = acceptée
)