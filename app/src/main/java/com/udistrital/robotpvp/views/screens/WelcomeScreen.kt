package com.udistrital.robotpvp.views.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.udistrital.robotpvp.data.enums.GameDifficulty

@Composable
fun WelcomeScreen(clickGame: (GameDifficulty) -> Unit) {
    var isToggled by rememberSaveable { mutableStateOf(false) }
    var showDifficultyDialog by rememberSaveable { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isToggled) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "info_icon_scale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1A2E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = " RobotPVP",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE94560),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡El duelo de los robots!",
                fontSize = 18.sp,
                color = Color(0xFFB0B0C0),
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))


            Button(
                onClick = { showDifficultyDialog = true },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE94560),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = " Jugar vs Máquina",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {},
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D2D44),
                    contentColor = Color(0xFFB0B0C0)
                )
            ) {
                Text(
                    text = "Jugar Online",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = if (isToggled) Color(0xFFE94560).copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { isToggled = !isToggled },
                    modifier = Modifier
                        .size(48.dp)
                        .scale(scale)
                ) {
                    Text(
                        text = if (isToggled) "✕" else "ⓘ",
                        fontSize = 28.sp,
                        color = if (isToggled) Color(0xFFE94560) else Color(0xFF6C6C8A)
                    )
                }
            }

            if (isToggled) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Elige tu oponente y prepárate para el combate robótico.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8AA8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "v1.0.0",
                fontSize = 12.sp,
                color = Color(0xFF4A4A62)
            )
        }
    }

    // Diálogo de Selección de Dificultad
    if (showDifficultyDialog) {
        Dialog(onDismissRequest = { showDifficultyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(2.dp, Color(0xFF38BDF8)),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SELECCIONA DIFICULTAD",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Elige el tiempo de supervivencia para ganar:",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Botón Fácil (30s)
                    Button(
                        onClick = {
                            showDifficultyDialog = false
                            clickGame(GameDifficulty.EASY)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Fácil (30s)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón Normal (45s)
                    Button(
                        onClick = {
                            showDifficultyDialog = false
                            clickGame(GameDifficulty.NORMAL)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Normal (45s)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón Difícil (60s)
                    Button(
                        onClick = {
                            showDifficultyDialog = false
                            clickGame(GameDifficulty.HARD)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Difícil (60s)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { showDifficultyDialog = false }) {
                        Text(
                            text = "Cancelar",
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}
