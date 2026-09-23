package com.udistrital.robotpvp

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
import com.udistrital.robotpvp.composables.GameScreen
import com.udistrital.robotpvp.composables.WelcomeScreen
import com.udistrital.robotpvp.enums.GameDifficulty
import com.udistrital.robotpvp.enums.TypeScreen
import com.udistrital.robotpvp.ui.theme.RobotPVPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobotPVPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var currentScreen by rememberSaveable { mutableStateOf(TypeScreen.WELCOME) }
                    var selectedDifficulty by rememberSaveable { mutableStateOf(GameDifficulty.EASY) }

                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        if (currentScreen == TypeScreen.WELCOME) {
                            WelcomeScreen { difficulty ->
                                selectedDifficulty = difficulty
                                currentScreen = TypeScreen.GAME
                            }
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
}
