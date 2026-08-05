package sn.uncgh.citoyen.ui.carte

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import sn.uncgh.citoyen.R
import sn.uncgh.citoyen.data.local.AppDatabase
import sn.uncgh.citoyen.data.remote.RetrofitInstance
import sn.uncgh.citoyen.data.repository.IncidentRepository

class CarteActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    private val viewModel: CarteViewModel by viewModels {
        val dao = AppDatabase.getDatabase(this).incidentDao()
        val repository = IncidentRepository(dao, RetrofitInstance.api)
        CarteViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance().userAgentValue = packageName

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carte)

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(GeoPoint(13.8925, -15.9350))

        viewModel.incidents.observe(this) { liste ->
            mapView.overlays.clear()
            liste.forEach { incident ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(incident.latitude, incident.longitude)
                marker.title = incident.categorieId
                marker.snippet = incident.statut
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                marker.icon = ContextCompat.getDrawable(this, iconeParPriorite(incident.priorite))
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
    }

    private fun iconeParPriorite(priorite: Int): Int = when (priorite) {
        3 -> R.drawable.marker_haute
        2 -> R.drawable.marker_moyenne
        else -> R.drawable.marker_basse
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}