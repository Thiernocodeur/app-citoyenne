package sn.uncgh.citoyen.ui.signalement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sn.uncgh.citoyen.data.local.entity.Incident
import sn.uncgh.citoyen.databinding.ItemIncidentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncidentAdapter(
    private val afficherVote: Boolean = false,
    private val onVoter: ((Incident) -> Unit)? = null
) : ListAdapter<Incident, IncidentAdapter.IncidentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val binding = ItemIncidentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncidentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IncidentViewHolder(private val binding: ItemIncidentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(incident: Incident) {
            binding.tvCategorie.text = incident.categorieId
            binding.tvDescription.text = incident.description
            binding.tvVotes.text = "${incident.nombreVotes} vote(s)"

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
            binding.tvDate.text = sdf.format(Date(incident.dateCreation))

            binding.tvStatut.text = libelleStatut(incident.statut)
            binding.tvStatut.setBackgroundColor(couleurStatut(incident.statut))

            binding.btnVoter.visibility = if (afficherVote) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnVoter.setOnClickListener { onVoter?.invoke(incident) }
        }

        private fun libelleStatut(statut: String): String = when (statut) {
            "RECU" -> "Reçu"
            "EN_COURS" -> "En cours"
            "RESOLU" -> "Résolu"
            "CLOS" -> "Clos"
            else -> statut
        }

        private fun couleurStatut(statut: String): Int = when (statut) {
            "RECU" -> android.graphics.Color.parseColor("#FFA726")
            "EN_COURS" -> android.graphics.Color.parseColor("#42A5F5")
            "RESOLU" -> android.graphics.Color.parseColor("#66BB6A")
            "CLOS" -> android.graphics.Color.parseColor("#BDBDBD")
            else -> android.graphics.Color.GRAY
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Incident>() {
        override fun areItemsTheSame(oldItem: Incident, newItem: Incident) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Incident, newItem: Incident) = oldItem == newItem
    }
}