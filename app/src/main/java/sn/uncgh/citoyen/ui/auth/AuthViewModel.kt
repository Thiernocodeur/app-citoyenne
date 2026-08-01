package sn.uncgh.citoyen.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    fun connexion(email: String, motDePasse: String) {
        if (email.isBlank() || motDePasse.isBlank()) {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, motDePasse).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Échec de la connexion")
            }
        }
    }

    fun inscription(email: String, motDePasse: String) {
        if (email.isBlank() || motDePasse.isBlank()) {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs")
            return
        }
        if (motDePasse.length < 6) {
            _authState.value = AuthState.Error("Le mot de passe doit contenir au moins 6 caractères")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, motDePasse).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Échec de l'inscription")
            }
        }
    }

    fun estConnecte(): Boolean {
        return firebaseAuth.currentUser != null
    }
}