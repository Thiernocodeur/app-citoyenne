package sn.uncgh.citoyen.ui.signalement

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import sn.uncgh.citoyen.data.local.AppDatabase
import sn.uncgh.citoyen.data.remote.RetrofitInstance
import sn.uncgh.citoyen.data.repository.IncidentRepository
import sn.uncgh.citoyen.databinding.ActivityCarteIncidentsBinding

class TousSignalementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarteIncidentsBinding
    private lateinit var repository: IncidentRepository
    private lateinit var adapter: IncidentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarteIncidentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).incidentDao()
        repository = IncidentRepository(dao, RetrofitInstance.api)

        adapter = IncidentAdapter(afficherVote = true) { incident ->
            lifecycleScope.launch {
                repository.voter(incident.id)
                Toast.makeText(this@TousSignalementsActivity, "Vote enregistré", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvTousIncidents.layoutManager = LinearLayoutManager(this)
        binding.rvTousIncidents.adapter = adapter

        repository.getAllIncidents().observe(this) { liste ->
            adapter.submitList(liste)
        }
    }
}