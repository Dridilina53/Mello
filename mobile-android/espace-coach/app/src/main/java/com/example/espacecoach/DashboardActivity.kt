package com.example.espacecoach

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.espacecoach.databinding.ActivityDashboardBinding

/** Écran 2 : le Dashboard du coach. */
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Qui est connecté ? Si personne, on retourne à la connexion.
        val coach = FakeBackend.coachConnecte
        if (coach == null) {
            allerALaConnexion()
            return
        }

        // Affiche "Bonjour Rahma !" (le prénom = le premier mot du nom)
        val prenom = coach.nom.substringBefore(" ")
        binding.tvBonjour.text = "Bonjour $prenom !"

        // Clic sur la carte "Gérer mes séances"
        binding.cardGererSeances.setOnClickListener {
            startActivity(Intent(this, GererSeancesActivity::class.java))
        }

        // Clic sur la carte "Mes demandes"
        binding.cardDemandes.setOnClickListener {
            startActivity(Intent(this, DemandesActivity::class.java))
        }

        // Clic sur "Se déconnecter"
        binding.btnDeconnexion.setOnClickListener {
            FakeBackend.deconnecter()
            allerALaConnexion()
        }
    }

    private fun allerALaConnexion() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // ferme le Dashboard : le bouton "retour" ne le rouvre pas
    }
}