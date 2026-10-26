package com.udistrital.robotpvp.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.udistrital.robotpvp.views.screens.GameScreen
import com.udistrital.robotpvp.views.screens.WelcomeScreen
import com.udistrital.robotpvp.views.screens.OnlineScreen
import com.udistrital.robotpvp.views.screens.WaitingScreen
import com.udistrital.robotpvp.views.screens.GAME_CODE_LENGTH
import com.udistrital.robotpvp.data.enums.GameDifficulty
import com.udistrital.robotpvp.data.enums.TypeScreen
import com.udistrital.robotpvp.views.theme.RobotPVPTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobotPVPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var currentScreen by rememberSaveable { mutableStateOf(TypeScreen.WELCOME) }
                    var selectedDifficulty by rememberSaveable { mutableStateOf(GameDifficulty.EASY) }
                    var gameCode by rememberSaveable { mutableStateOf("") }
                    var isHost by rememberSaveable { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        if (currentScreen == TypeScreen.WELCOME) {
                            WelcomeScreen(
                                clickGame = { difficulty ->
                                    selectedDifficulty = difficulty
                                    currentScreen = TypeScreen.GAME
                                },
                                clickOnline = { currentScreen = TypeScreen.ONLINE }
                            )
                        } else if (currentScreen == TypeScreen.ONLINE) {
                            OnlineScreen(
                                onCreateGame = {
                                    gameCode = generateGameCode()
                                    isHost = true
                                    currentScreen = TypeScreen.WAITING
                                },
                                onJoinGame = { code ->
                                    gameCode = code
                                    isHost = false
                                    currentScreen = TypeScreen.WAITING
                                },
                                onBack = { currentScreen = TypeScreen.WELCOME }
                            )
                        } else if (currentScreen == TypeScreen.WAITING) {
                            WaitingScreen(
                                gameCode = gameCode,
                                isHost = isHost,
                                onCancel = { currentScreen = TypeScreen.ONLINE }
                            )
                        } else if (currentScreen == TypeScreen.GAME) {
                            GameScreen(
                                gameDurationSeconds = selectedDifficulty.durationSeconds,
                                onBackToMenu = {
                                    currentScreen = TypeScreen.WELCOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Genera un código aleatorio sin caracteres confusos (0/O, 1/I)
    private fun generateGameCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..GAME_CODE_LENGTH).map { chars.random() }.joinToString("")
    }
}