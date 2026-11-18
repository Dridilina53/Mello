package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
                Toast.makeText(this, "Bienvenue $email", Toast.LENGTH_LONG).show()
            }
        }

        tvCreerCompte.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}