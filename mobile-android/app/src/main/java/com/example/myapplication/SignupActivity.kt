
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        val spinnerRole = findViewById<Spinner>(R.id.spinnerRole)
        val layoutCoach = findViewById<LinearLayout>(R.id.layoutCoach)
        val spinnerSpecialite = findViewById<Spinner>(R.id.spinnerSpecialite)
        val edtExperience = findViewById<EditText>(R.id.edtExperience)
        val edtDiplome = findViewById<EditText>(R.id.edtDiplome)
        val edtBio = findViewById<EditText>(R.id.edtBio)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val edtConfirm = findViewById<EditText>(R.id.edtConfirm)
        val btnCreerCompte = findViewById<Button>(R.id.btnCreerCompte)
        val tvSeConnecter = findViewById<TextView>(R.id.tvSeConnecter)

        val roles = arrayOf("Client", "Coach")
        val adapterRole = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )
        adapterRole.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerRole.adapter = adapterRole

        val specialites = arrayOf(
            "Choisir...",
            "Gestion du stress",
            "Confiance en soi",
            "Carrière",
            "Relations",
            "Bien-être",
            "Autre"
        )

        val adapterSpecialite = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            specialites
        )
        adapterSpecialite.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerSpecialite.adapter = adapterSpecialite

        spinnerRole.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    layoutCoach.visibility =
                        if (position == 1) View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        btnRetour.setOnClickListener {
            finish()
        }

        btnCreerCompte.setOnClickListener {

            val nom = edtNom.text.toString().trim()
            val prenom = edtPrenom.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val telephone = edtTelephone.text.toString().trim()
            val dateNaissance = edtNaissance.text.toString().trim()

            val role = if (spinnerRole.selectedItemPosition == 1) {
                "coach"
            } else {
                "client"
            }

            val specialite = if (role == "coach") {
                spinnerSpecialite.selectedItem.toString()
            } else {
                ""
            }

            val experience =
                edtExperience.text.toString().toIntOrNull() ?: 0

            val diplome = edtDiplome.text.toString().trim()
            val bio = edtBio.text.toString().trim()
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

            if (role == "coach" &&
                spinnerSpecialite.selectedItemPosition == 0
            ) {
                Toast.makeText(
                    this,
                    "Veuillez choisir votre spécialité",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (role == "coach" && diplome.isEmpty()) {
                Toast.makeText(
                    this,
                    "Veuillez indiquer votre diplôme",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (experience < 0) {
                Toast.makeText(
                    this,
                    "Expérience invalide",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnCreerCompte.isEnabled = false
            btnCreerCompte.text = "Inscription..."

            // Préparer les données à envoyer
            val donnees = JSONObject()

            donnees.put("nom", nom)
            donnees.put("prenom", prenom)
            donnees.put("email", email)
            donnees.put("telephone", telephone)
            donnees.put("dateNaissance", dateNaissance)
            donnees.put("role", role)
            donnees.put("specialite", specialite)
            donnees.put("experience", experience)
            donnees.put("diplome", diplome)
            donnees.put("bio", bio)
            donnees.put("password", password)

            // Envoyer les données au backend
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

                        if (role == "client") {
                            val intent = Intent(
                                this,
                                ClientActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            val intent = Intent(
                                this,
                                LoginActivity::class.java
                            )
                            startActivity(intent)
                        }

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
