
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.time.LocalDate

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnRetour = findViewById<TextView>(R.id.btnRetour)
        val edtNom = findViewById<EditText>(R.id.edtNom)
        val edtPrenom = findViewById<EditText>(R.id.edtPrenom)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtTelephone = findViewById<EditText>(R.id.edtTelephone)
        val edtNaissance = findViewById<EditText>(R.id.edtNaissance)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val edtConfirm = findViewById<EditText>(R.id.edtConfirm)
        val btnCreerCompte = findViewById<Button>(R.id.btnCreerCompte)
        val tvSeConnecter = findViewById<TextView>(R.id.tvSeConnecter)

        btnRetour.setOnClickListener {
            finish()
        }

        btnCreerCompte.setOnClickListener {

            val nom = edtNom.text.toString().trim()
            val prenom = edtPrenom.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val telephone = edtTelephone.text.toString().trim()
            val dateNaissance = edtNaissance.text.toString().trim()
            val password = edtPass.text.toString()
            val confirm = edtConfirm.text.toString()

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() ||
                telephone.isEmpty() || dateNaissance.isEmpty() ||
                password.isEmpty() || confirm.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Remplissez tous les champs",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!telephone.matches(Regex("^[0-9]{8}$"))) {
                Toast.makeText(
                    this,
                    "Le téléphone doit contenir 8 chiffres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            try {
                LocalDate.parse(dateNaissance)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Date invalide. Format : AAAA-MM-JJ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Mot de passe : 6 caractères minimum",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(
                    this,
                    "Les mots de passe ne correspondent pas",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnCreerCompte.isEnabled = false
            btnCreerCompte.text = "Inscription..."

            val donnees = JSONObject().apply {
                put("nom", nom)
                put("prenom", prenom)
                put("email", email)
                put("telephone", telephone)
                put("dateNaissance", dateNaissance)
                put("role", "client")
                put("password", password)
            }

            Thread {

                var message = ""
                var succes = false

                try {
                    val url = URL(
                        "http://10.0.2.2:8080/api/auth/register"
                    )

                    val connexion =
                        url.openConnection() as HttpURLConnection

                    connexion.requestMethod = "POST"
                    connexion.connectTimeout = 10000
                    connexion.readTimeout = 10000
                    connexion.doOutput = true

                    connexion.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )

                    val writer = OutputStreamWriter(
                        connexion.outputStream,
                        Charsets.UTF_8
                    )

                    writer.write(donnees.toString())
                    writer.flush()
                    writer.close()

                    val code = connexion.responseCode

                    val flux = if (code in 200..299) {
                        connexion.inputStream
                    } else {
                        connexion.errorStream
                    }

                    val reponse = flux.bufferedReader().use {
                        it.readText()
                    }

                    val resultat = JSONObject(reponse)

                    message = resultat.optString(
                        "message",
                        "Réponse du serveur"
                    )

                    succes = code in 200..299

                    connexion.disconnect()

                } catch (e: Exception) {
                    message = "Erreur de connexion au serveur"
                }

                runOnUiThread {

                    btnCreerCompte.isEnabled = true
                    btnCreerCompte.text = "S'inscrire"

                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                    ).show()

                    if (succes) {
                        val intent = Intent(
                            this,
                            ClientActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    }
                }

            }.start()
        }

        tvSeConnecter.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
