
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.ZoneId

class ClientActivity : AppCompatActivity() {

    private lateinit var adapter: SeanceClientAdapter

    private val baseUrl = "http://10.0.2.2:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        val btnDeconnexion = findViewById<Button>(R.id.btnDeconnexion)
        val rvSeances = findViewById<RecyclerView>(R.id.rvSeancesClient)
        val tvEtat = findViewById<TextView>(R.id.tvEtatSeances)

        adapter = SeanceClientAdapter { seance ->
            envoyerDemande(seance)
        }

        rvSeances.layoutManager = LinearLayoutManager(this)
        rvSeances.adapter = adapter

        btnDeconnexion.setOnClickListener {
            getSharedPreferences("MelloSession", MODE_PRIVATE)
                .edit().clear().apply()

            getSharedPreferences("MelloClient", MODE_PRIVATE)
                .edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        tvEtat.text = "Chargement des séances..."
        chargerSeances()
    }

    private fun chargerSeances() {
        Thread {
            var connexion: HttpURLConnection? = null

            try {
                connexion = URL("$baseUrl/seances")
                    .openConnection() as HttpURLConnection

                connexion.requestMethod = "GET"
                connexion.connectTimeout = 10000
                connexion.readTimeout = 10000

                val code = connexion.responseCode

                if (code == HttpURLConnection.HTTP_OK) {
                    val reponse = connexion.inputStream.bufferedReader().use {
                        it.readText()
                    }

                    val tableau = JSONArray(reponse)
                    val liste = mutableListOf<SeanceClient>()

                    for (i in 0 until tableau.length()) {
                        val obj = tableau.getJSONObject(i)

                        val dateBrute = obj.optString("dateSeance", "")
                        val dateLocale = try {
                            Instant.parse(dateBrute)
                                .atZone(ZoneId.of("Africa/Tunis"))
                                .toLocalDate()
                                .toString()
                        } catch (e: Exception) {
                            dateBrute.take(10)
                        }

                        liste.add(
                            SeanceClient(
                                idSeance = obj.optInt("idSeance"),
                                coachId = obj.optInt("coachId"),
                                coachNom = obj.optString("coachNom", "Coach"),
                                specialite = obj.optString("specialite", ""),
                                titre = obj.optString("titre", "Séance"),
                                description = obj.optString("description", ""),
                                dateSeance = dateLocale,
                                heureDebut = obj.optString("heureDebut", "").take(5),
                                dureeMinutes = obj.optInt("dureeMinutes"),
                                prix = obj.optDouble("prix")
                            )
                        )
                    }

                    runOnUiThread {
                        adapter.setSeances(liste)

                        val tvEtat = findViewById<TextView>(R.id.tvEtatSeances)

                        tvEtat.visibility = if (liste.isEmpty()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                        if (liste.isEmpty()) {
                            tvEtat.text = "Aucune séance disponible pour le moment."
                        }
                    }

                } else {
                    runOnUiThread {
                        findViewById<TextView>(R.id.tvEtatSeances).text =
                            "Erreur serveur : HTTP $code"
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    findViewById<TextView>(R.id.tvEtatSeances).text =
                        "Erreur de chargement : ${e.message}"

                    Toast.makeText(
                        this,
                        "Impossible de charger les séances",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                connexion?.disconnect()
            }
        }.start()
    }

    private fun envoyerDemande(seance: SeanceClient) {
        val prefs = getSharedPreferences("MelloSession", MODE_PRIVATE)
        val clientId = prefs.getInt("userId", -1)

        if (clientId == -1) {
            Toast.makeText(
                this,
                "Identifiant client introuvable. Reconnectez-vous.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        Thread {
            var connexion: HttpURLConnection? = null

            try {
                connexion = URL("$baseUrl/reservations")
                    .openConnection() as HttpURLConnection

                connexion.requestMethod = "POST"
                connexion.connectTimeout = 10000
                connexion.readTimeout = 10000
                connexion.doOutput = true
                connexion.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                )

                val json = JSONObject().apply {
                    put("clientId", clientId)
                    put("titreSeance", seance.titre)
                    put("coachNom", seance.coachNom)
                    put("specialite", seance.specialite)
                    put("dateSeance", seance.dateSeance)
                    put("heureDebut", seance.heureDebut)
                    put("dureeMinutes", seance.dureeMinutes)
                    put("prix", seance.prix)
                    put("message", "")
                }

                connexion.outputStream.use {
                    it.write(json.toString().toByteArray(Charsets.UTF_8))
                }

                val code = connexion.responseCode

                runOnUiThread {
                    if (code in 200..299) {
                        Toast.makeText(
                            this,
                            "Demande envoyée avec succès !",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val erreur = connexion?.errorStream
                            ?.bufferedReader()?.use { it.readText() }

                        Toast.makeText(
                            this,
                            "Erreur $code : ${erreur ?: "Réservation impossible"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                connexion?.disconnect()
            }
        }.start()
    }
}
