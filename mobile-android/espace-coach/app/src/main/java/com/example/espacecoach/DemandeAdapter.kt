package com.example.espacecoach

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.espacecoach.databinding.ItemDemandeBinding

/**
 * L'adapter fabrique une carte (item_demande.xml) pour chaque demande de la liste.
 * Les deux fonctions reçues (onAccepter, onSupprimer) sont appelées quand on clique.
 */
class DemandeAdapter(
    private val onAccepter: (Demande) -> Unit,
    private val onSupprimer: (Demande) -> Unit
) : RecyclerView.Adapter<DemandeAdapter.DemandeViewHolder>() {

    private var demandes: List<Demande> = emptyList()

    /** Donne une nouvelle liste à l'adapter et redessine les cartes. */
    fun setDemandes(nouvelles: List<Demande>) {
        demandes = nouvelles
        notifyDataSetChanged()
    }

    // Le ViewHolder garde les éléments d'UNE carte
    class DemandeViewHolder(val binding: ItemDemandeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemandeViewHolder {
        val binding = ItemDemandeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DemandeViewHolder(binding)
    }

    override fun getItemCount(): Int = demandes.size

    override fun onBindViewHolder(holder: DemandeViewHolder, position: Int) {
        val demande = demandes[position]
        val b = holder.binding

        b.tvClient.text = demande.clientNom
        b.tvSeance.text = demande.seanceTitre
        b.tvQuand.text = "${demande.date} à ${demande.heure}"

        if (demande.acceptee) {
            b.tvStatut.text = "✓ Acceptée"
            b.tvStatut.setTextColor(Color.parseColor("#2E7D32")) // vert
            b.btnAccepter.visibility = View.GONE // plus besoin du bouton
        } else {
            b.tvStatut.text = "En attente"
            b.tvStatut.setTextColor(Color.parseColor("#7D5A44")) // marron
            b.btnAccepter.visibility = View.VISIBLE
        }

        b.btnAccepter.setOnClickListener { onAccepter(demande) }
        b.btnSupprimer.setOnClickListener { onSupprimer(demande) }
    }
}