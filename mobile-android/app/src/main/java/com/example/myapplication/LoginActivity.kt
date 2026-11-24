
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

            val email = edtEmail.text.toString()
            val pass = edtPass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {

                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()

            } else {

                // Connexion avec le backend
                Thread {

                    try {

                        val url = URL("http://10.0.2.2:8080/api/auth/login")
                        val connexion = url.openConnection() as HttpURLConnection

                        connexion.requestMethod = "POST"
                        connexion.setRequestProperty("Content-Type", "application/json")
                        connexion.doOutput = true

                        val donnees = JSONObject()
                        donnees.put("email", email)
                        donnees.put("password", pass)

                        val writer = OutputStreamWriter(connexion.outputStream)
                        writer.write(donnees.toString())
                        writer.flush()
                        writer.close()

                        val code = connexion.responseCode

                        val flux = if (code == 200) {
                            connexion.inputStream
                        } else {
                            connexion.errorStream
                        }

                        val reader = BufferedReader(InputStreamReader(flux))
                        val reponse = reader.readText()
                        reader.close()

                        val resultat = JSONObject(reponse)
                        val message = resultat.optString("message")

                        runOnUiThread {

                            if (code == 200) {

                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, ClientActivity::class.java)
                                startActivity(intent)

                            } else {

                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            }
                        }

                        connexion.disconnect()

                    } catch (e: Exception) {

                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Erreur de connexion au serveur : ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }.start()
            }
        }

        tvCreerCompte.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
