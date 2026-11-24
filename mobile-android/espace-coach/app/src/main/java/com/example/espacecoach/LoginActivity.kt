package com.example.espacecoach

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.espacecoach.databinding.ActivityLoginBinding

/** Écran 1 : le coach se connecte. */
class LoginActivity : AppCompatActivity() {

    // "binding" donne accès aux éléments du XML (binding.etEmail, binding.btnConnexion...)
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Au clic sur le bouton, on lance la connexion
        binding.btnConnexion.setOnClickListener { seConnecter() }
    }

    private fun seConnecter() {
        val email = binding.etEmail.text.toString().trim()
        val motDePasse = binding.etMotDePasse.text.toString()

        // 1. Les champs sont-ils remplis ?
        if (email.isEmpty() || motDePasse.isEmpty()) {
            montrerErreur("Veuillez remplir tous les champs.")
            return
        }

        // 2. On demande au backend de vérifier les identifiants
        val coach = FakeBackend.login(email, motDePasse)
        if (coach == null) {
            montrerErreur("E-mail ou mot de passe incorrect.")
            return
        }

        // 3. Connexion réussie : on ouvre le Dashboard
        binding.tvErreur.visibility = View.GONE
        startActivity(Intent(this, DashboardActivity::class.java))
        finish() // ferme la connexion : le bouton "retour" ne la rouvre pas
    }

    private fun montrerErreur(message: String) {
        binding.tvErreur.text = message
        binding.tvErreur.visibility = View.VISIBLE
    }
}