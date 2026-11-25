package com.example.espacecoach

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.espacecoach.databinding.ItemSeanceBinding

/** Fabrique une carte (item_seance.xml) pour chaque séance de la liste. */
class SeanceAdapter : RecyclerView.Adapter<SeanceAdapter.SeanceViewHolder>() {

    private var seances: List<Seance> = emptyList()

    /** Donne une nouvelle liste à l'adapter et redessine les cartes. */
    fun setSeances(nouvelles: List<Seance>) {
        seances = nouvelles
        notifyDataSetChanged()
    }

    // Le ViewHolder garde les éléments d'UNE carte
    class SeanceViewHolder(val binding: ItemSeanceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeanceViewHolder {
        val binding = ItemSeanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeanceViewHolder(binding)
    }

    override fun getItemCount(): Int = seances.size

    override fun onBindViewHolder(holder: SeanceViewHolder, position: Int) {
        val s = seances[position]
        val b = holder.binding

        b.tvTitre.text = s.titre
        b.tvDetails.text = "${s.specialite} · ${s.mode}"
        b.tvQuand.text = "${s.date} à ${s.heure} · ${s.dureeMinutes} min"
        b.tvPrix.text = "${s.prix} DT"
    }
}