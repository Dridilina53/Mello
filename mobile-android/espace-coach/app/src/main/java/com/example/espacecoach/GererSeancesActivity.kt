package com.example.espacecoach

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.espacecoach.databinding.ActivityGererSeancesBinding

/** Écran 3 : "Gérer mes séances" avec la liste de cartes. */
class GererSeancesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGererSeancesBinding
    private val adapter = SeanceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGererSeancesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // La liste de cartes
        binding.rvSeances.layoutManager = LinearLayoutManager(this)
        binding.rvSeances.adapter = adapter

        // "‹ Retour" : on revient au Dashboard
        binding.tvRetour.setOnClickListener { finish() }

        // "+ Créer une séance" : on ouvre le formulaire
        binding.btnCreerSeance.setOnClickListener {
            startActivity(Intent(this, FormulaireSeanceActivity::class.java))
        }
    }

    // onResume est appelé à chaque fois que l'écran redevient visible
    // (donc aussi quand on revient du formulaire)
    override fun onResume() {
        super.onResume()
        afficherSeances()
    }

    /** Demande les séances au Backend et les affiche en cartes. */
    private fun afficherSeances() {
        val seances = FakeBackend.getSeances()
        adapter.setSeances(seances)
        // Aucune séance : on montre le message. Sinon : on le cache.
        binding.tvVide.visibility = if (seances.isEmpty()) View.VISIBLE else View.GONE
    }
}