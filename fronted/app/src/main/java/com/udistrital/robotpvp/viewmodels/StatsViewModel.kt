package com.udistrital.robotpvp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.udistrital.robotpvp.data.models.PlayerStats
import com.udistrital.robotpvp.repositories.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {
    private val repository = ScoreRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _stats = MutableStateFlow<PlayerStats?>(null)
    val stats: StateFlow<PlayerStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadStats() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            _error.value = "Debes iniciar sesión para ver tus estadísticas"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getStats(uid)
                .onSuccess { _stats.value = it }
                .onFailure { _error.value = "No se pudo conectar con el servidor: ${it.message}" }
            _isLoading.value = false
        }
    }

    // Guarda el resultado de una partida. Si falla, no interrumpe el juego.
    fun saveGame(score: Int, winner: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.saveGame(uid, score, winner)
        }
    }
}
