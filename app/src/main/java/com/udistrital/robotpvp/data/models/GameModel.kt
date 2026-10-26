package com.udistrital.robotpvp.data.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class GameModel(
    val robotRadius: Float = 60f,
    val borderWidth: Float = 20f,
    val elasticity: Float = 0.75f,
    val friction: Float = 0.985f,
    val blueSpeed: Float = 12f,
    val accelSensitivity: Float = 0.15f,

    val robot1Pos: Offset = Offset(300f, 400f),
    val robot1VelX: Float = 0f,
    val robot1VelY: Float = 0f,

    val robot2Pos: Offset = Offset.Zero,
    val robot2VelX: Float = 0f,
    val robot2VelY: Float = 0f,

    val targetBorderPos: Offset = Offset.Zero,
    val canvasSize: Size = Size.Zero
)