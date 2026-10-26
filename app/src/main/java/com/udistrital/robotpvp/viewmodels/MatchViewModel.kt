package com.udistrital.robotpvp.viewmodels

import androidx.lifecycle.ViewModel
import com.udistrital.robotpvp.repositories.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel: ViewModel() {
    private val repository = MatchRepository()

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    fun createCode(){
        _code.value = repository.createCode()
    }
}