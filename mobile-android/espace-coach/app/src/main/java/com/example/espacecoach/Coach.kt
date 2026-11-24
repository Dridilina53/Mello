package com.example.espacecoach

// Un coach : il a un nom et des identifiants de connexion.
data class Coach(
    val nom: String,
    val email: String,
    val motDePasse: String
)
