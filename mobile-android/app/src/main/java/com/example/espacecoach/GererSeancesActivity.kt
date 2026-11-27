
package com.example.espacecoach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityGererSeancesBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GererSeancesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGererSeancesBinding
    private val adapter = SeanceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGererSeancesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvSeances.layoutManager = LinearLayoutManager(this)
        binding.rvSeances.adapter = adapter

        binding.tvRetour.setOnClickListener {
            finish()
        }

        binding.btnCreerSeance.setOnClickListener {
            startActivity(
                Intent(this, FormulaireSeanceActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        afficherSeances()
    }

    private fun afficherSeances() {

        val prefs = getSharedPreferences("MelloSession", MODE_PRIVATE)
        val coachId = prefs.getInt("userId", -1)

        if (coachId == -1) {
            Toast.makeText(this, "Coach non connecté", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            var connexion: HttpURLConnection? = null

            try {
                val url = URL(
                    "http://10.0.2.2:8080/api/seances/coach/$coachId"
                )

                connexion = url.openConnection() as HttpURLConnection
                connexion.requestMethod = "GET"
                connexion.connectTimeout = 10000
                connexion.readTimeout = 10000

                val code = connexion.responseCode

                if (code == HttpURLConnection.HTTP_OK) {

                    val reponse = connexion.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                    val tableau = JSONArray(reponse)
                    val liste = mutableListOf<Seance>()

                    for (i in 0 until tableau.length()) {

                        val obj: JSONObject = tableau.getJSONObject(i)

                        val dateBrute = obj.optString("dateSeance")
                        val heureDebut = obj.optString("heureDebut")
                        val heureFin = obj.optString("heureFin")

                        val date = try {
                            val morceaux = dateBrute.substring(0, 10).split("-")
                            "${morceaux[2]}/${morceaux[1]}/${morceaux[0]}"
                        } catch (e: Exception) {
                            dateBrute
                        }

                        val heure = heureDebut.take(5)

                        val duree = try {
                            val debut = java.time.LocalTime.parse(heureDebut)
                            val fin = java.time.LocalTime.parse(heureFin)
                            java.time.Duration.between(debut, fin).toMinutes().toInt()
                        } catch (e: Exception) {
                            0
                        }

                        val seance = Seance(
                            id = obj.optInt("idSeance"),
                            coachEmail = prefs.getString("email", "") ?: "",
                            coachNom = prefs.getString("coachNom", "") ?: "",
                            titre = obj.optString("titre"),
                            specialite = "Spécialité ${obj.optInt("idSpecialite")}",
                            date = date,
                            heure = heure,
                            dureeMinutes = duree,
                            prix = obj.optDouble("prix", 0.0).toInt(),
                            mode = "Non précisé"
                        )

                        liste.add(seance)
                    }

                    runOnUiThread {
                        adapter.setSeances(liste)
                        binding.tvVide.visibility =
                            if (liste.isEmpty()) View.VISIBLE else View.GONE
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Erreur serveur : $code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur de connexion : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                connexion?.disconnect()
            }

        }.start()
    }
}
