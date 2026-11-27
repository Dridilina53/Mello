package com.example.espacecoach

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemSeanceBinding

class SeanceAdapter :
    RecyclerView.Adapter<SeanceAdapter.SeanceViewHolder>() {

    private var seances: List<Seance> = emptyList()

    fun setSeances(liste: List<Seance>) {
        seances = liste
        notifyDataSetChanged()
    }

    class SeanceViewHolder(
        val binding: ItemSeanceBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeanceViewHolder {
        val binding = ItemSeanceBinding.inflate(
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

        b.tvTitre.text = seance.titre
        b.tvDetails.text = "${seance.specialite} · ${seance.mode}"
        b.tvQuand.text =
            "${seance.date} à ${seance.heure} · ${seance.dureeMinutes} min"
        b.tvPrix.text = "${seance.prix} DT"
    }
}