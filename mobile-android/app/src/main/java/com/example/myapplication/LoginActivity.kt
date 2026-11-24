
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnRetour = findViewById<TextView>(R.id.btnRetour)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnSeConnecter = findViewById<Button>(R.id.btnSeConnecter)
        val tvCreerCompte = findViewById<TextView>(R.id.tvCreerCompte)

        btnRetour.setOnClickListener {
            finish()
        }

        btnSeConnecter.setOnClickListener {

            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                    this,
                    "Remplissez tous les champs",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnSeConnecter.isEnabled = false

            Thread {
                var connexion: HttpURLConnection? = null

                try {
                    val url = URL("http://10.0.2.2:8080/api/auth/login")
                    connexion = url.openConnection() as HttpURLConnection

                    connexion.requestMethod = "POST"
                    connexion.setRequestProperty("Content-Type", "application/json")
                    connexion.connectTimeout = 10000
                    connexion.readTimeout = 10000
                    connexion.doOutput = true

                    val donnees = JSONObject()
                    donnees.put("email", email)
                    donnees.put("password", pass)

                    OutputStreamWriter(connexion.outputStream).use {
                        it.write(donnees.toString())
                    }

                    val code = connexion.responseCode

                    val flux = if (code == 200) {
                        connexion.inputStream
                    } else {
                        connexion.errorStream
                    }

                    val reponse = flux?.bufferedReader()?.use {
                        it.readText()
                    } ?: ""

                    val resultat = JSONObject(reponse)
                    val message = resultat.optString(
                        "message",
                        "Une erreur est survenue."
                    )

                    val role = resultat.optString("role")
                    val userId = resultat.optInt("userId", -1)

                    runOnUiThread {
                        btnSeConnecter.isEnabled = true

                        if (code == 200) {
                            when (role.uppercase()) {

                                "CLIENT" -> {
                                    Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(
                                        this,
                                        ClientActivity::class.java
                                    )
                                    intent.putExtra("userId", userId)
                                    intent.putExtra("email", email)
                                    startActivity(intent)
                                }

                                "COACH" -> {
                                    Toast.makeText(
                                        this,
                                        "Connexion réussie ! Espace coach en cours de préparation.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                else -> {
                                    Toast.makeText(
                                        this,
                                        "Rôle utilisateur non reconnu.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } else {
                            Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        btnSeConnecter.isEnabled = true

                        Toast.makeText(
                            this,
                            "Erreur de connexion au serveur : ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } finally {
                    connexion?.disconnect()
                }

            }.start()
        }

        tvCreerCompte.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
