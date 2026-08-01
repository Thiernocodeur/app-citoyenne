package sn.uncgh.citoyen.data.repository

import androidx.lifecycle.LiveData
import sn.uncgh.citoyen.data.local.dao.IncidentDao
import sn.uncgh.citoyen.data.local.entity.Incident
import sn.uncgh.citoyen.data.remote.IncidentApiService

class IncidentRepository(
    private val incidentDao: IncidentDao,
    private val apiService: IncidentApiService
) {

    fun getAllIncidents(): LiveData<List<Incident>> {
        return incidentDao.getAllIncidents()
    }

    fun getIncidentsByCitoyen(citoyenId: String): LiveData<List<Incident>> {
        return incidentDao.getIncidentsByCitoyen(citoyenId)
    }

    suspend fun creerSignalement(incident: Incident) {
        // 1. Sauvegarde locale immédiate (fonctionne même hors ligne)
        incidentDao.insert(incident)

        // 2. Tentative de synchronisation avec le serveur
        try {
            val response = apiService.createIncident(incident)
            if (response.isSuccessful) {
                incidentDao.update(incident.copy(synchronise = true))
            }
        } catch (e: Exception) {
            // Pas de connexion ou erreur réseau : l'incident reste en local,
            // il sera synchronisé plus tard via synchroniserIncidentsEnAttente()
        }
    }

    suspend fun voter(incidentId: String) {
        incidentDao.incrementerVotes(incidentId)
        try {
            apiService.voterPourIncident(incidentId)
        } catch (e: Exception) {
            // Le vote reste enregistré localement, sera synchronisé plus tard
        }
    }

    suspend fun synchroniserIncidentsEnAttente() {
        val incidentsNonSync = incidentDao.getIncidentsNonSynchronises()
        for (incident in incidentsNonSync) {
            try {
                val response = apiService.createIncident(incident)
                if (response.isSuccessful) {
                    incidentDao.update(incident.copy(synchronise = true))
                }
            } catch (e: Exception) {
                // Sera retenté à la prochaine synchronisation
            }
        }
    }

    suspend fun changerStatut(incidentId: String, nouveauStatut: String) {
        incidentDao.updateStatut(incidentId, nouveauStatut)
        try {
            apiService.updateStatut(incidentId, mapOf("statut" to nouveauStatut))
        } catch (e: Exception) {
            // Sera synchronisé plus tard
        }
    }
}

