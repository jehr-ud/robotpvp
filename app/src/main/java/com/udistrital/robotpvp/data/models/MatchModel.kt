package com.udistrital.robotpvp.data.models

import com.udistrital.robotpvp.data.enums.GameDifficulty
import com.udistrital.robotpvp.data.enums.MatchStatus

data class MatchModel(
    var code: String = "",
    var player1Uid: String = "", // host player
    var player2Uid: String = "",
    var startAt: String = "",
    var turnPlayerUid: String = "",
    var winnerPlayerUid: String = "",
    var difficulty: String = GameDifficulty.EASY.name,
    var gameDurationSeconds: Float = 30f,
    var isGameOver: Boolean = false,
    var timeRemaining: Float = 30f,
    var createdAt: String = "",
    var finishedAt: String = "",
    var game: GameModel = GameModel(),
    var status: String = MatchStatus.WAITING.name
)