package com.udistrital.robotpvp.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.udistrital.robotpvp.enums.TypeScreen

@Composable
fun WelcomeScreen(clickGame: () -> Unit){
    var isToggled by rememberSaveable { mutableStateOf(false) }

    Column(){
        Text("Welcome to RobotPVP")

        Button(
            onClick=clickGame
        ) {
            Text("Play vs Machine")
        }

        Button(
            onClick={}
        ) {
            Text("Play Online")
        }

        IconButton(
            onClick = { isToggled = !isToggled }
        ) {
            Text(
                text = if (isToggled) "ⓘ" else "ⓘ",
                fontSize = 24.sp
            )
        }
    }
}

