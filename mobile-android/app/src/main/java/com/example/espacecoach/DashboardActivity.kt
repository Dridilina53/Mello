package com.example.espacecoach

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.LoginActivity
import com.example.myapplication.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = getSharedPreferences("MelloSession", MODE_PRIVATE)
        val role = session.getString("role", "")
        val coachNom = session.getString("coachNom", "")

        if (!role.equals("COACH", ignoreCase = true) || coachNom.isNullOrBlank()) {
            allerALaConnexion()
            return
        }

        val prenom = coachNom.substringBefore(" ")
        binding.tvBonjour.text = "Bonjour  !"

        binding.cardGererSeances.setOnClickListener {
            startActivity(Intent(this, GererSeancesActivity::class.java))
        }

        binding.cardDemandes.setOnClickListener {
            startActivity(Intent(this, DemandesActivity::class.java))
        }

        binding.btnDeconnexion.setOnClickListener {
            session.edit().clear().apply()
            allerALaConnexion()
        }
    }

    private fun allerALaConnexion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
