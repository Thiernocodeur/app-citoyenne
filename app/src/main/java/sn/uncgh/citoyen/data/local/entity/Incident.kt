package sn.uncgh.citoyen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey val id: String,
    val citoyenId: String,
    val description: String,
    val photoUrl: String?,
    val categorieId: String,
    val latitude: Double,
    val longitude: Double,
    val priorite: Int,
    val statut: String,
    val dateCreation: Long,
    val nombreVotes: Int = 0,
    val synchronise: Boolean = false
)