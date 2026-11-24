package com.example.espacecoach

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.espacecoach.databinding.ActivityDemandesBinding

/** Écran : la liste des demandes. Le coach peut accepter ou supprimer. */
class DemandesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemandesBinding
    private lateinit var adapter: DemandeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemandesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRetour.setOnClickListener { finish() }

        // On crée l'adapter en lui disant quoi faire à chaque clic
        adapter = DemandeAdapter(
            onAccepter = { demande ->
                FakeBackend.accepterDemande(demande.id)
                Toast.makeText(this, "Demande acceptée", Toast.LENGTH_SHORT).show()
                afficherDemandes()
            },
            onSupprimer = { demande ->
                FakeBackend.supprimerDemande(demande.id)
                Toast.makeText(this, "Demande supprimée", Toast.LENGTH_SHORT).show()
                afficherDemandes()
            }
        )
        binding.rvDemandes.layoutManager = LinearLayoutManager(this)
        binding.rvDemandes.adapter = adapter

        afficherDemandes()
    }

    /** Demande la liste au Backend et l'affiche. */
    private fun afficherDemandes() {
        val demandes = FakeBackend.getDemandes()
        adapter.setDemandes(demandes)
        // Si la liste est vide, on montre le message à la place
        binding.tvVide.visibility = if (demandes.isEmpty()) View.VISIBLE else View.GONE
    }
}