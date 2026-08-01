package sn.uncgh.citoyen.data.remote

import retrofit2.Response
import retrofit2.http.*
import sn.uncgh.citoyen.data.local.entity.Incident

interface IncidentApiService {

    @GET("incidents")
    suspend fun getAllIncidents(): Response<List<Incident>>

    @GET("incidents/{id}")
    suspend fun getIncidentById(@Path("id") id: String): Response<Incident>

    @POST("incidents")
    suspend fun createIncident(@Body incident: Incident): Response<Incident>

    @PUT("incidents/{id}/statut")
    suspend fun updateStatut(
        @Path("id") id: String,
        @Body statut: Map<String, String>
    ): Response<Incident>

    @POST("incidents/{id}/vote")
    suspend fun voterPourIncident(@Path("id") id: String): Response<Incident>
}

