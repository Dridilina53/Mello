package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemClientSeanceBinding

class SeanceClientAdapter(
    private val onDemande: (SeanceClient) -> Unit
) : RecyclerView.Adapter<SeanceClientAdapter.SeanceViewHolder>() {

    private var seances: List<SeanceClient> = emptyList()

    fun setSeances(liste: List<SeanceClient>) {
        seances = liste
        notifyDataSetChanged()
    }

    class SeanceViewHolder(
        val binding: ItemClientSeanceBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeanceViewHolder {
        val binding = ItemClientSeanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SeanceViewHolder(binding)
    }

    override fun getItemCount(): Int = seances.size

    override fun onBindViewHolder(holder: SeanceViewHolder, position: Int) {
        val seance = seances[position]
        val b = holder.binding

        b.tvCoachClient.text = "Coach : ${seance.coachNom}"
        b.tvTitreClient.text = seance.titre
        b.tvSpecialiteClient.text = seance.specialite
        b.tvDescriptionClient.text = seance.description
        b.tvDateClient.text = "Date : ${seance.dateSeance}"
        b.tvHeureClient.text =
            "Heure : ${seance.heureDebut} · ${seance.dureeMinutes} min"
        b.tvPrixClient.text = "${seance.prix} DT"

        b.btnDemandeClient.setOnClickListener {
            onDemande(seance)
        }
    }
}