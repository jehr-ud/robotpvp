package com.udistrital.robotpvp.data.models

// Resumen del jugador que devuelve GET /v1/score/stats/user/:uid
data class PlayerStats(
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val bestScore: Int = 0
)
