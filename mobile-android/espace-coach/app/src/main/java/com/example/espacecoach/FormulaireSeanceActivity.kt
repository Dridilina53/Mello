package com.example.espacecoach

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.espacecoach.databinding.ActivityFormulaireSeanceBinding
import java.util.Calendar

/** Écran 4 : le formulaire pour créer une séance. */
class FormulaireSeanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormulaireSeanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulaireSeanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRetour.setOnClickListener { finish() }
        binding.etDate.setOnClickListener { choisirDate() }
        binding.etHeure.setOnClickListener { choisirHeure() }
        binding.btnEnregistrer.setOnClickListener { enregistrer() }
    }

    /** Ouvre le calendrier et écrit la date choisie dans le champ. */
    private fun choisirDate() {
        val aujourdhui = Calendar.getInstance()
        val fenetre = DatePickerDialog(
            this,
            { _, annee, mois, jour ->
                // le mois commence à 0 en Android, donc on ajoute 1
                binding.etDate.setText(String.format("%02d/%02d/%d", jour, mois + 1, annee))
            },
            aujourdhui.get(Calendar.YEAR),
            aujourdhui.get(Calendar.MONTH),
            aujourdhui.get(Calendar.DAY_OF_MONTH)
        )
        fenetre.datePicker.minDate = System.currentTimeMillis() - 1000 // pas de date passée
        fenetre.show()
    }

    /** Ouvre l'horloge et écrit l'heure choisie dans le champ. */
    private fun choisirHeure() {
        TimePickerDialog(
            this,
            { _, heure, minute ->
                binding.etHeure.setText(String.format("%02d:%02d", heure, minute))
            },
            9, 0, true // heure de départ 09:00, format 24 h
        ).show()
    }

    /** Vérifie le formulaire, puis l'enregistre. */
    private fun enregistrer() {
        val titre = binding.etTitre.text.toString().trim()
        val specialite = binding.etSpecialite.text.toString().trim()
        val date = binding.etDate.text.toString()
        val heure = binding.etHeure.text.toString()
        val duree = binding.etDuree.text.toString().toIntOrNull()
        val prix = binding.etPrix.text.toString().toIntOrNull()
        val mode = if (binding.rbVisio.isChecked) "Visio" else "Présentiel"

        // 1. Tous les champs doivent être remplis
        if (titre.isEmpty() || specialite.isEmpty() || date.isEmpty() ||
            heure.isEmpty() || duree == null || prix == null
        ) {
            montrerErreur("Veuillez remplir tous les champs.")
            return
        }
        // 2. La durée doit être positive
        if (duree <= 0) {
            montrerErreur("La durée doit être supérieure à 0.")
            return
        }

        // 3. Tout est bon : on envoie la séance au Backend
        val seance = FakeBackend.saveSeance(titre, specialite, date, heure, duree, prix, mode)
        if (seance == null) {
            montrerErreur("Erreur : vous n'êtes pas connecté.")
            return
        }
        Toast.makeText(this, "Séance enregistrée !", Toast.LENGTH_SHORT).show()
        finish() // retour à "Gérer mes séances"
    }

    private fun montrerErreur(message: String) {
        binding.tvErreur.text = message
        binding.tvErreur.visibility = View.VISIBLE
    }
}