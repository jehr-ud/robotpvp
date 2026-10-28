package com.udistrital.robotpvp.data.models

data class GameModel(
    var robotRadius: Float = 60f,
    var borderWidth: Float = 20f,
    var elasticity: Float = 0.75f,
    var friction: Float = 0.985f,
    var blueSpeed: Float = 12f,
    var accelSensitivity: Float = 0.15f,
    var robot1PosX: Float = 300f,
    var robot1PosY: Float = 400f,
    var robot1VelX: Float = 0f,
    var robot1VelY: Float = 0f,
    var robot2PosX: Float = 0f,
    var robot2PosY: Float = 0f,
    var robot2VelX: Float = 0f,
    var robot2VelY: Float = 0f,
    var targetBorderPosX: Float = 0f,
    var targetBorderPosY: Float = 0f,
    var canvasWidth: Float = 0f,
    var canvasHeight: Float = 0f
)