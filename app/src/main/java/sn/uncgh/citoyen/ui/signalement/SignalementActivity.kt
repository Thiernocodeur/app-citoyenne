package sn.uncgh.citoyen.ui.signalement



import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import sn.uncgh.citoyen.data.local.AppDatabase
import sn.uncgh.citoyen.data.local.entity.Incident
import sn.uncgh.citoyen.data.remote.RetrofitInstance
import sn.uncgh.citoyen.data.repository.IncidentRepository
import sn.uncgh.citoyen.databinding.ActivitySignalementBinding
import java.io.File
import java.util.UUID

class SignalementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignalementBinding
    private lateinit var repository: IncidentRepository

    private var photoUri: Uri? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var positionRecuperee = false

    private val categories = listOf("Voirie", "Éclairage public", "Ordures", "Inondation", "Réseaux", "Insécurité")

    private val lancerCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { succes ->
        if (succes) {
            binding.ivPhoto.setImageURI(photoUri)
        }
    }

    private val lancerGalerie = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri
            binding.ivPhoto.setImageURI(uri)
        }
    }

    private val demandePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            recupererPosition()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignalementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).incidentDao()
        repository = IncidentRepository(dao, RetrofitInstance.api)

        binding.spinnerCategorie.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )

        demanderPermissionsEtPosition()

        binding.btnPrendrePhoto.setOnClickListener { ouvrirCamera() }
        binding.btnChoisirGalerie.setOnClickListener { lancerGalerie.launch("image/*") }
        binding.btnEnvoyer.setOnClickListener { envoyerSignalement() }

        binding.btnHistorique.setOnClickListener {
            startActivity(android.content.Intent(this, HistoriqueActivity::class.java))
        }

        binding.btnDeconnexion.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            startActivity(android.content.Intent(this, sn.uncgh.citoyen.ui.auth.LoginActivity::class.java))
            finish()
        }

        binding.btnTousSignalements.setOnClickListener {
            startActivity(android.content.Intent(this, TousSignalementsActivity::class.java))
        }
    }

    private fun demanderPermissionsEtPosition() {
        val permissionsNecessaires = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val manquantes = permissionsNecessaires.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (manquantes.isNotEmpty()) {
            demandePermissions.launch(manquantes.toTypedArray())
        } else {
            recupererPosition()
        }
    }

    private fun recupererPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                positionRecuperee = true
                binding.tvLocalisation.text = "Position : %.5f, %.5f".format(latitude, longitude)
            } else {
                binding.tvLocalisation.text = "Position : indisponible (activez le GPS)"
            }
        }
    }

    private fun ouvrirCamera() {
        val fichierPhoto = File(getExternalFilesDir("Pictures"), "incident_${UUID.randomUUID()}.jpg")
        photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", fichierPhoto)
        lancerCamera.launch(photoUri)
    }

    private fun envoyerSignalement() {
        val description = binding.etDescription.text.toString().trim()
        val categorie = binding.spinnerCategorie.selectedItem?.toString() ?: ""
        val priorite = when (binding.rgPriorite.checkedRadioButtonId) {
            binding.rbBasse.id -> 1
            binding.rbHaute.id -> 3
            else -> 2
        }

        if (description.isBlank()) {
            Toast.makeText(this, "Veuillez décrire l'incident", Toast.LENGTH_SHORT).show()
            return
        }
        if (!positionRecuperee) {
            Toast.makeText(this, "Position GPS non disponible, réessayez", Toast.LENGTH_SHORT).show()
            return
        }

        val citoyenId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonyme"

        val incident = Incident(
            id = UUID.randomUUID().toString(),
            citoyenId = citoyenId,
            description = description,
            photoUrl = photoUri?.toString(),
            categorieId = categorie,
            latitude = latitude,
            longitude = longitude,
            priorite = priorite,
            statut = "RECU",
            dateCreation = System.currentTimeMillis()
        )

        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnEnvoyer.isEnabled = false

        lifecycleScope.launch {
            repository.creerSignalement(incident)
            binding.progressBar.visibility = android.view.View.GONE
            Toast.makeText(this@SignalementActivity, "Signalement envoyé", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}