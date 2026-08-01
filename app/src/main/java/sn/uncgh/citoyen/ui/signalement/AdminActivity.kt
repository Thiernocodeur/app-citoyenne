package sn.uncgh.citoyen.ui.signalement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import sn.uncgh.citoyen.data.local.AppDatabase
import sn.uncgh.citoyen.data.remote.RetrofitInstance
import sn.uncgh.citoyen.data.repository.IncidentRepository
import sn.uncgh.citoyen.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var repository: IncidentRepository
    private lateinit var adapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).incidentDao()
        repository = IncidentRepository(dao, RetrofitInstance.api)

        adapter = AdminAdapter { incident, nouveauStatut ->
            lifecycleScope.launch {
                repository.changerStatut(incident.id, nouveauStatut)
            }
        }
        binding.rvIncidents.layoutManager = LinearLayoutManager(this)
        binding.rvIncidents.adapter = adapter

        repository.getAllIncidents().observe(this) { liste ->
            adapter.submitList(liste)
            afficherStatistiques(liste)
        }
    }

    private fun afficherStatistiques(liste: List<sn.uncgh.citoyen.data.local.entity.Incident>) {
        val total = liste.size
        val recus = liste.count { it.statut == "RECU" }
        val enCours = liste.count { it.statut == "EN_COURS" }
        val resolus = liste.count { it.statut == "RESOLU" }
        binding.tvStats.text = "Total : $total  •  Reçus : $recus  •  En cours : $enCours  •  Résolus : $resolus"
    }
}