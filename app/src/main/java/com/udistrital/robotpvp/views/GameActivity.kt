package com.udistrital.robotpvp.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.udistrital.robotpvp.data.enums.GameDifficulty
import com.udistrital.robotpvp.data.enums.TypeScreen
import com.udistrital.robotpvp.viewmodels.MatchViewModel
import com.udistrital.robotpvp.views.screens.GameScreen
import com.udistrital.robotpvp.views.screens.OnlineScreen
import com.udistrital.robotpvp.views.screens.WaitingScreen
import com.udistrital.robotpvp.views.screens.WelcomeScreen
import com.udistrital.robotpvp.views.theme.RobotPVPTheme

class GameActivity : ComponentActivity() {
    private val matchViewModel: MatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobotPVPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var currentScreen by rememberSaveable { mutableStateOf(TypeScreen.WELCOME) }
                    var selectedDifficulty by rememberSaveable { mutableStateOf(GameDifficulty.EASY) }
                    var isHost by rememberSaveable { mutableStateOf(false) }

                    val match by matchViewModel.match.collectAsState()
                    val code by matchViewModel.code.collectAsState()
                    val error by matchViewModel.error.collectAsState()

                    LaunchedEffect(error) {
                        error?.let {
                            Toast.makeText(this@GameActivity, it, Toast.LENGTH_SHORT).show()
                            matchViewModel.clearError()
                        }
                    }

                    LaunchedEffect(match) {
                        match?.let {
                            if ((it.status == com.udistrital.robotpvp.data.enums.MatchStatus.READY.name ||
                                        it.status == com.udistrital.robotpvp.data.enums.MatchStatus.PLAYING.name) &&
                                currentScreen == TypeScreen.WAITING
                            ) {
                                // Move to game when both players are ready
                                selectedDifficulty = try {
                                    com.udistrital.robotpvp.data.enums.GameDifficulty.valueOf(it.difficulty)
                                } catch (e: Exception) {
                                    GameDifficulty.EASY
                                }
                                currentScreen = TypeScreen.GAME
                            }
                            if (it.status == com.udistrital.robotpvp.data.enums.MatchStatus.FINISHED.name &&
                                it.isGameOver
                            ) {
                                // Game finished
                            }
                        }
                    }

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
                                    isHost = true
                                    matchViewModel.createMatch(selectedDifficulty)
                                    currentScreen = TypeScreen.WAITING
                                },
                                onJoinGame = { joinCode ->
                                    isHost = false
                                    matchViewModel.joinMatch(joinCode)
                                    currentScreen = TypeScreen.WAITING
                                },
                                onBack = { currentScreen = TypeScreen.WELCOME }
                            )
                        } else if (currentScreen == TypeScreen.WAITING) {
                            WaitingScreen(
                                gameCode = code.ifEmpty { match?.code ?: "" },
                                isHost = isHost,
                                onCancel = {
                                    matchViewModel.leaveMatch()
                                    currentScreen = TypeScreen.ONLINE
                                }
                            )
                        } else if (currentScreen == TypeScreen.GAME) {
                            GameScreen(
                                gameDurationSeconds = selectedDifficulty.durationSeconds,
                                onBackToMenu = {
                                    matchViewModel.leaveMatch()
                                    currentScreen = TypeScreen.WELCOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}