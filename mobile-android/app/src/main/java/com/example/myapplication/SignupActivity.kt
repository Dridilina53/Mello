package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
        val edtVille = findViewById<EditText>(R.id.edtVille)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val edtConfirm = findViewById<EditText>(R.id.edtConfirm)
        val btnCreerCompte = findViewById<Button>(R.id.btnCreerCompte)
        val tvSeConnecter = findViewById<TextView>(R.id.tvSeConnecter)

        btnRetour.setOnClickListener {
            finish()
        }

        btnCreerCompte.setOnClickListener {
            val nom = edtNom.text.toString()
            val prenom = edtPrenom.text.toString()
            val email = edtEmail.text.toString()
            val tel = edtTelephone.text.toString()
            val naissance = edtNaissance.text.toString()
            val ville = edtVille.text.toString()
            val pass = edtPass.text.toString()
            val confirm = edtConfirm.text.toString()

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || tel.isEmpty() ||
                naissance.isEmpty() || ville.isEmpty() || pass.isEmpty() || confirm.isEmpty()
            ) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 8) {
                Toast.makeText(this, "Mot de passe : 8 caractères minimum", Toast.LENGTH_SHORT).show()
            } else if (pass != confirm) {
                Toast.makeText(this, "Les mots de passe ne sont pas identiques", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Compte créé pour $prenom $nom", Toast.LENGTH_LONG).show()
            }
        }

        tvSeConnecter.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}