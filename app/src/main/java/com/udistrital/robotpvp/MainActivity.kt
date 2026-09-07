package com.udistrital.robotpvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.udistrital.robotpvp.composables.GameScreen
import com.udistrital.robotpvp.composables.WelcomeScreen
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


                    if (currentScreen == TypeScreen.WELCOME) {
                        WelcomeScreen() {
                            currentScreen = TypeScreen.GAME
                        }
                    } else if (currentScreen == TypeScreen.GAME) {
                        GameScreen()
                    }
                }
            }
        }
    }
}
