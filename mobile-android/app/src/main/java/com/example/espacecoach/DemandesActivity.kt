
package com.example.espacecoach

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityDemandesBinding
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DemandesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemandesBinding
    private lateinit var adapter: DemandeAdapter

    private val baseUrl = "http://10.0.2.2:8080/api/reservations"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDemandesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DemandeAdapter(
            onAccepter = { demande ->
                modifierDemande(demande.id, "accepter")
            },
            onRefuser = { demande ->
                modifierDemande(demande.id, "refuser")
            }
        )

        binding.rvDemandes.layoutManager = LinearLayoutManager(this)
        binding.rvDemandes.adapter = adapter
        binding.tvRetour.setOnClickListener { finish() }

        afficherDemandes()
    }

    private fun afficherDemandes() {
        val prefs = getSharedPreferences("MelloSession", MODE_PRIVATE)
        val coachNom = prefs.getString("coachNom", "") ?: ""

        if (coachNom.isBlank()) {
            Toast.makeText(
                this,
                "ERREUR : nom du coach introuvable dans MelloSession",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        Thread {
            var connexion: HttpURLConnection? = null

            try {
                val nomEncode = URLEncoder.encode(coachNom, "UTF-8")
                val url = URL("$baseUrl/coach?coachNom=$nomEncode")

                connexion = url.openConnection() as HttpURLConnection
                connexion.requestMethod = "GET"
                connexion.connectTimeout = 10000
                connexion.readTimeout = 10000

                val code = connexion.responseCode

                if (code == HttpURLConnection.HTTP_OK) {

                    val reponse = connexion.inputStream.bufferedReader().use {
                        it.readText()
                    }

                    val tableau = JSONArray(reponse)
                    val liste = mutableListOf<Demande>()

                    for (i in 0 until tableau.length()) {
                        val obj = tableau.getJSONObject(i)

                        liste.add(
                            Demande(
                                id = obj.optInt("id"),
                                clientNom = obj.optString("clientNom", "Client"),
                                seanceTitre = obj.optString("titreSeance", "Séance"),
                                date = obj.optString("dateSeance", "").take(10),
                                heure = obj.optString("heureDebut", "").take(5),
                                statut = obj.optString("statut", "EN_ATTENTE"),
                                message = obj.optString("message", "")
                            )
                        )
                    }

                    runOnUiThread {
                        adapter.setDemandes(liste)

                        binding.tvVide.visibility =
                            if (liste.isEmpty()) View.VISIBLE else View.GONE

                        Toast.makeText(
                            this,
                            "Coach : $coachNom | Demandes : ${liste.size}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    val erreur = connexion.errorStream?.bufferedReader()?.use {
                        it.readText()
                    } ?: "Aucun détail"

                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Erreur HTTP $code : $erreur",
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

    private fun modifierDemande(id: Int, action: String) {
        val prefs = getSharedPreferences("MelloSession", MODE_PRIVATE)
        val coachNom = prefs.getString("coachNom", "") ?: ""

        Thread {
            var connexion: HttpURLConnection? = null

            try {
                val nomEncode = URLEncoder.encode(coachNom, "UTF-8")
                val url = URL("$baseUrl/$id/$action?coachNom=$nomEncode")

                connexion = url.openConnection() as HttpURLConnection
                connexion.requestMethod = "PUT"
                connexion.connectTimeout = 10000
                connexion.readTimeout = 10000

                val code = connexion.responseCode

                runOnUiThread {
                    if (code in 200..299) {
                        val message = if (action == "accepter") {
                            "Demande acceptée"
                        } else {
                            "Demande refusée"
                        }

                        Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        afficherDemandes()

                    } else {
                        Toast.makeText(
                            this,
                            "Erreur serveur : $code",
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
