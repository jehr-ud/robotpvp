package com.udistrital.robotpvp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.robotpvp.data.enums.GameDifficulty
import com.udistrital.robotpvp.data.models.MatchModel
import com.udistrital.robotpvp.repositories.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchViewModel : ViewModel() {
    private val repository = MatchRepository()

    private val _match = MutableStateFlow<MatchModel?>(null)
    val match: StateFlow<MatchModel?> = _match.asStateFlow()

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createMatch(difficulty: GameDifficulty = GameDifficulty.EASY) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.createMatch(difficulty).onSuccess { match ->
                _match.value = match
                _code.value = match.code
            }.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }

    fun joinMatch(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.joinMatch(code).onSuccess { match ->
                _match.value = match
                _code.value = match.code
            }.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }

    fun observeMatch(code: String) {
        viewModelScope.launch {
            repository.observeMatch(code).collect { match ->
                _match.value = match
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun leaveMatch() {
        val currentCode = _code.value
        if (currentCode.isNotEmpty()) {
            viewModelScope.launch {
                repository.leaveMatch(currentCode)
            }
        }
        _match.value = null
        _code.value = ""
    }
}