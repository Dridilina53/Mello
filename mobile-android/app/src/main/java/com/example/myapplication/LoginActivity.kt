package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.espacecoach.DashboardActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnConnexion = findViewById<Button>(R.id.btnSeConnecter)

        btnConnexion.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPass.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnConnexion.isEnabled = false
            btnConnexion.text = "Connexion..."

            Thread {
                var message = ""
                var succes = false
                var userId = -1
                var role = ""
                var coachNom = ""

                try {
                    val url = URL("http://10.0.2.2:8080/api/auth/login")
                    val connection = url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json")

                    val body = JSONObject().apply {
                        put("email", email)
                        put("password", password)
                    }

                    connection.outputStream.use {
                        it.write(body.toString().toByteArray(Charsets.UTF_8))
                    }

                    val code = connection.responseCode
                    val stream = if (code in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                    val response = stream.bufferedReader().use { it.readText() }
                    connection.disconnect()

                    val data = JSONObject(response)

                    if (code in 200..299) {
                        role = data.optString("role").uppercase()
                        userId = data.optInt("userId", -1)
                        coachNom = data.optString("coachNom")
                        succes = role == "CLIENT" || role == "COACH"

                        if (!succes) {
                            message = "Rôle utilisateur non reconnu."
                        } else if (userId == -1) {
                            succes = false
                            message = "Identifiant utilisateur invalide."
                        } else if (role == "COACH" && coachNom.isBlank()) {
                            succes = false
                            message = "Le nom du Coach est absent de la réponse du serveur."
                        }
                    } else {
                        message = data.optString("message", "Identifiants incorrects.")
                    }

                } catch (e: Exception) {
                    message = "Erreur de connexion au serveur."
                }

                runOnUiThread {
                    btnConnexion.isEnabled = true
                    btnConnexion.text = "Se connecter"

                    if (succes) {
                        // Enregistrer la session commune
                        getSharedPreferences("MelloSession", MODE_PRIVATE)
                            .edit()
                            .putInt("userId", userId)
                            .putString("email", email)
                            .putString("role", role)
                            .putString("coachNom", coachNom)
                            .apply()

                        if (role == "CLIENT") {
                            // Conserver les préférences déjà utilisées par l'espace Client
                            getSharedPreferences("MelloClient", MODE_PRIVATE)
                                .edit()
                                .putInt("userId", userId)
                                .putString("email", email)
                                .apply()

                            startActivity(Intent(this, ClientActivity::class.java))
                        } else {
                            // Ouvrir l'espace Coach
                            startActivity(Intent(this, DashboardActivity::class.java))
                        }

                        finish()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }
    }
}
