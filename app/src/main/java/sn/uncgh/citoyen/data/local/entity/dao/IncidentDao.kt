package sn.uncgh.citoyen.data.local.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import sn.uncgh.citoyen.data.local.entity.Incident

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: Incident)

    @Update
    suspend fun update(incident: Incident)

    @Query("SELECT * FROM incidents WHERE citoyenId = :citoyenId ORDER BY dateCreation DESC")
    fun getIncidentsByCitoyen(citoyenId: String): LiveData<List<Incident>>

    @Query("SELECT * FROM incidents ORDER BY dateCreation DESC")
    fun getAllIncidents(): LiveData<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getIncidentById(id: String): Incident?

    @Query("SELECT * FROM incidents WHERE synchronise = 0")
    suspend fun getIncidentsNonSynchronises(): List<Incident>

    @Query("UPDATE incidents SET statut = :nouveauStatut WHERE id = :id")
    suspend fun updateStatut(id: String, nouveauStatut: String)

    @Query("UPDATE incidents SET nombreVotes = nombreVotes + 1 WHERE id = :id")
    suspend fun incrementerVotes(id: String)

    @Delete
    suspend fun delete(incident: Incident)
}