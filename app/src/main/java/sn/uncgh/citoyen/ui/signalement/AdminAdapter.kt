package sn.uncgh.citoyen.ui.signalement

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sn.uncgh.citoyen.data.local.entity.Incident
import sn.uncgh.citoyen.databinding.ItemIncidentAdminBinding

class AdminAdapter(
    private val onStatutChange: (Incident, String) -> Unit
) : ListAdapter<Incident, AdminAdapter.AdminViewHolder>(DiffCallback()) {

    private val statutsPossibles = listOf("RECU", "EN_COURS", "RESOLU", "CLOS")
    private val statutsLibelles = listOf("Reçu", "En cours", "Résolu", "Clos")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val binding = ItemIncidentAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdminViewHolder(private val binding: ItemIncidentAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(incident: Incident) {
            binding.tvCategorie.text = incident.categorieId
            binding.tvDescription.text = incident.description
            binding.tvPriorite.text = "Priorité : ${libellePriorite(incident.priorite)}"
            binding.tvStatut.text = libelleStatut(incident.statut)
            binding.tvStatut.setBackgroundColor(couleurStatut(incident.statut))

            val adapter = ArrayAdapter(
                binding.root.context, android.R.layout.simple_spinner_dropdown_item, statutsLibelles
            )
            binding.spinnerStatut.adapter = adapter
            binding.spinnerStatut.setSelection(statutsPossibles.indexOf(incident.statut))

            binding.spinnerStatut.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    val nouveauStatut = statutsPossibles[position]
                    if (nouveauStatut != incident.statut) {
                        onStatutChange(incident, nouveauStatut)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        private fun libellePriorite(p: Int) = when (p) { 1 -> "Basse"; 3 -> "Haute"; else -> "Moyenne" }

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