package sn.uncgh.citoyen.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import sn.uncgh.citoyen.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Si déjà connecté, on saute directement à l'écran principal
        if (authViewModel.estConnecte()) {
            allerVersAccueil()
            return
        }

        binding.btnConnexion.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val motDePasse = binding.etMotDePasse.text.toString().trim()
            authViewModel.connexion(email, motDePasse)
        }

        binding.btnInscription.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val motDePasse = binding.etMotDePasse.text.toString().trim()
            authViewModel.inscription(email, motDePasse)
        }

        binding.btnAdmin.setOnClickListener {
            startActivity(Intent(this, sn.uncgh.citoyen.ui.signalement.AdminActivity::class.java))
        }

        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Idle -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.tvErreur.visibility = android.view.View.GONE
                }
                is AuthState.Loading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                    binding.tvErreur.visibility = android.view.View.GONE
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                    allerVersAccueil()
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.tvErreur.visibility = android.view.View.VISIBLE
                    binding.tvErreur.text = state.message
                }
            }
        }
    }

    private fun allerVersAccueil() {
        startActivity(Intent(this, sn.uncgh.citoyen.ui.signalement.SignalementActivity::class.java))
        finish()
    }
}