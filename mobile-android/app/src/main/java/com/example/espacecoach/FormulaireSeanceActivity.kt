package com.example.espacecoach

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityFormulaireSeanceBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.concurrent.Executors

class FormulaireSeanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormulaireSeanceBinding
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulaireSeanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRetour.setOnClickListener { finish() }
        binding.etDate.setOnClickListener { choisirDate() }
        binding.etHeure.setOnClickListener { choisirHeure() }
        binding.btnEnregistrer.setOnClickListener { enregistrer() }

        val session = getSharedPreferences("MelloSession", MODE_PRIVATE)
        if (session.getString("role", "") != "COACH" ||
            session.getInt("userId", -1) == -1) {
            montrerErreur("Veuillez vous connecter avec un compte coach.")
            binding.btnEnregistrer.isEnabled = false
        }
    }

    private fun choisirDate() {
        val calendrier = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, annee, mois, jour ->
                binding.etDate.setText(
                    String.format("%02d/%02d/%04d", jour, mois + 1, annee)
                )
            },
            calendrier.get(Calendar.YEAR),
            calendrier.get(Calendar.MONTH),
            calendrier.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun choisirHeure() {
        TimePickerDialog(
            this,
            { _, heure, minute ->
                binding.etHeure.setText(String.format("%02d:%02d", heure, minute))
            },
            9, 0, true
        ).show()
    }

    private fun enregistrer() {
        val titre = binding.etTitre.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val heure = binding.etHeure.text.toString().trim()
        val duree = binding.etDuree.text.toString().toIntOrNull()
        val prix = binding.etPrix.text.toString().toDoubleOrNull()

        if (titre.isEmpty() || date.isEmpty() || heure.isEmpty() ||
            duree == null || prix == null) {
            montrerErreur("Veuillez remplir tous les champs.")
            return
        }

        if (duree <= 0 || prix < 0) {
            montrerErreur("Vérifiez la durée et le prix.")
            return
        }

        val dateBackend = try {
            val parties = date.split("/")
            "${parties[2]}-${parties[1]}-${parties[0]}"
        } catch (e: Exception) {
            montrerErreur("Date invalide.")
            return
        }

        val session = getSharedPreferences("MelloSession", MODE_PRIVATE)
        val coachId = session.getInt("userId", -1)

        if (coachId == -1) {
            montrerErreur("Session expirée. Reconnectez-vous.")
            return
        }

        val donnees = JSONObject().apply {
            put("coachId", coachId)
            put("titre", titre)
            put("description", "")
            put("dateSeance", dateBackend)
            put("heureDebut", "$heure:00")
            put("dureeMinutes", duree)
            put("prix", prix)
        }

        binding.btnEnregistrer.isEnabled = false
        binding.tvErreur.visibility = View.GONE

        executor.execute {
            var connexion: HttpURLConnection? = null

            try {
                connexion = (URL("http://10.0.2.2:8080/api/seances")
                    .openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 10000
                    readTimeout = 10000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                }

                connexion.outputStream.use {
                    it.write(donnees.toString().toByteArray(Charsets.UTF_8))
                }

                val code = connexion.responseCode
                val flux = if (code in 200..299) {
                    connexion.inputStream
                } else {
                    connexion.errorStream
                }

                val reponse = flux?.bufferedReader()?.use { it.readText() }.orEmpty()

                runOnUiThread {
                    binding.btnEnregistrer.isEnabled = true

                    if (code in 200..299) {
                        Toast.makeText(
                            this,
                            "Séance enregistrée !",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        montrerErreur("Erreur serveur ($code) : $reponse")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.btnEnregistrer.isEnabled = true
                    montrerErreur("Erreur de connexion : ${e.message}")
                }
            } finally {
                connexion?.disconnect()
            }
        }
    }

    private fun montrerErreur(message: String) {
        binding.tvErreur.text = message
        binding.tvErreur.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}