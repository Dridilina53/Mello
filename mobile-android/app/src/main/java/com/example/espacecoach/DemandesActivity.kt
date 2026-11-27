
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

                    android.util.Log.d(
                        "MELLO_JSON",
                        "Réponse backend : $reponse"
                    )

                    val tableau = JSONArray(reponse)
                    val liste = mutableListOf<Demande>()

                    for (i in 0 until tableau.length()) {
                        val obj = tableau.getJSONObject(i)

                        val demande = Demande(
                            id = obj.optInt("idReservation", -1),
                            clientNom = obj.optString("clientNom", "Client"),
                            seanceTitre = obj.optString("titreSeance", "Séance"),
                            date = obj.optString("dateSeance", "").take(10),
                            heure = obj.optString("heureDebut", "").take(5),
                            statut = obj.optString("statut", "EN_ATTENTE"),
                            message = obj.optString("message", "")
                        )

                        android.util.Log.d(
                            "MELLO_JSON",
                            "ID dans Demande : ${demande.id}"
                        )

                        liste.add(demande)
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

        if (coachNom.isBlank()) {
            Toast.makeText(
                this,
                "Nom du coach introuvable",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (id <= 0) {
            Toast.makeText(
                this,
                "Identifiant de réservation invalide : $id",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        Thread {
            var connexion: HttpURLConnection? = null

            try {
                val nomEncode = URLEncoder.encode(coachNom, "UTF-8")
                val url = URL("$baseUrl/$id/$action?coachNom=$nomEncode")

                android.util.Log.d("MELLO_DEBUG", "ID envoyé : $id")
                android.util.Log.d("MELLO_DEBUG", "URL envoyée : $url")

                connexion = url.openConnection() as HttpURLConnection
                connexion.requestMethod = "PUT"
                connexion.connectTimeout = 10000
                connexion.readTimeout = 10000

                val code = connexion.responseCode

                val stream = if (code in 200..299) {
                    connexion.inputStream
                } else {
                    connexion.errorStream
                }

                val reponse = stream?.bufferedReader()?.use {
                    it.readText()
                } ?: "Aucun détail fourni par le serveur"

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
                            Toast.LENGTH_LONG
                        ).show()

                        afficherDemandes()

                    } else {
                        Toast.makeText(
                            this,
                            "Erreur HTTP $code : $reponse",
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
