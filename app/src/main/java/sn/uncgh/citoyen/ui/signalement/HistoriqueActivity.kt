package sn.uncgh.citoyen.ui.signalement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import sn.uncgh.citoyen.data.local.AppDatabase
import sn.uncgh.citoyen.data.remote.RetrofitInstance
import sn.uncgh.citoyen.data.repository.IncidentRepository
import sn.uncgh.citoyen.databinding.ActivityHistoriqueBinding

class HistoriqueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoriqueBinding
    private lateinit var repository: IncidentRepository
    private lateinit var adapter: IncidentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoriqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).incidentDao()
        repository = IncidentRepository(dao, RetrofitInstance.api)

        adapter = IncidentAdapter()
        binding.rvHistorique.layoutManager = LinearLayoutManager(this)
        binding.rvHistorique.adapter = adapter

        val citoyenId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonyme"

        repository.getIncidentsByCitoyen(citoyenId).observe(this) { liste ->
            adapter.submitList(liste)
            binding.tvVide.visibility = if (liste.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            binding.rvHistorique.visibility = if (liste.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        }
    }
}