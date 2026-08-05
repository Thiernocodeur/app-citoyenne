package sn.uncgh.citoyen.ui.carte

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import sn.uncgh.citoyen.data.local.AppDatabase
import sn.uncgh.citoyen.data.local.entity.Incident
import sn.uncgh.citoyen.data.remote.RetrofitInstance
import sn.uncgh.citoyen.data.repository.IncidentRepository

class CarteViewModel(private val repository: IncidentRepository) : ViewModel() {
    val incidents: LiveData<List<Incident>> = repository.getAllIncidents()
}

class CarteViewModelFactory(private val repository: IncidentRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CarteViewModel(repository) as T
    }
}