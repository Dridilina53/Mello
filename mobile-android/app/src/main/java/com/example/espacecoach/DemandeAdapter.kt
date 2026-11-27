package com.example.espacecoach

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemDemandeBinding

class DemandeAdapter(
    private val onAccepter: (Demande) -> Unit,
    private val onRefuser: (Demande) -> Unit
) : RecyclerView.Adapter<DemandeAdapter.DemandeViewHolder>() {

    private var demandes: List<Demande> = emptyList()

    fun setDemandes(nouvelles: List<Demande>) {
        demandes = nouvelles
        notifyDataSetChanged()
    }

    class DemandeViewHolder(
        val binding: ItemDemandeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemandeViewHolder {
        val binding = ItemDemandeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DemandeViewHolder(binding)
    }

    override fun getItemCount() = demandes.size

    override fun onBindViewHolder(holder: DemandeViewHolder, position: Int) {
        val demande = demandes[position]
        val b = holder.binding

        b.tvClient.text = demande.clientNom
        b.tvSeance.text = demande.seanceTitre
        b.tvQuand.text = "${demande.date} à ${demande.heure}"

        if (demande.message.isNotBlank()) {
            b.tvSeance.text = "${demande.seanceTitre}\n${demande.message}"
        }

        b.tvStatut.text = when (demande.statut.uppercase()) {
            "CONFIRMEE" -> "✓ Acceptée"
            "ANNULEE" -> "Refusée"
            else -> "En attente"
        }

        b.tvStatut.setTextColor(
            when (demande.statut.uppercase()) {
                "CONFIRMEE" -> Color.rgb(46, 125, 50)
                "ANNULEE" -> Color.RED
                else -> Color.rgb(125, 90, 68)
            }
        )

        val enAttente = demande.statut.equals("EN_ATTENTE", true)
        b.btnAccepter.visibility = if (enAttente) View.VISIBLE else View.GONE
        b.btnSupprimer.visibility = if (enAttente) View.VISIBLE else View.GONE

        b.btnAccepter.setOnClickListener { onAccepter(demande) }
        b.btnSupprimer.setOnClickListener { onRefuser(demande) }
    }
}