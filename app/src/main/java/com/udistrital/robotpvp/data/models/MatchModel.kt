package com.udistrital.robotpvp.data.models

import com.udistrital.robotpvp.data.enums.GameDifficulty


data class MatchModel (
    var code: String,
    var player1Uid: String, // host player
    var player2Uid: String,
    var startAt: String,
    var turnPlayerUid: String,
    var winnerPlayerUid: String,

    var difficulty : GameDifficulty,
    val gameDurationSeconds: Float = 30f,
    val isGameOver: Boolean = false,
    val timeRemaining: Float = gameDurationSeconds,

    var createdAt: String,
    var finishedAt: String,

    var game: GameModel
)