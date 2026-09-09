package com.udistrital.robotpvp.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(clickGame: () -> Unit) {
    var isToggled by rememberSaveable { mutableStateOf(false) }

    // Animación de escala para el ícono de información
    val scale by animateFloatAsState(
        targetValue = if (isToggled) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "info_icon_scale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1A2E) // Fondo oscuro elegante
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título principal con estilo
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

            // Botón "Play vs Machine" con estilo
            Button(
                onClick = clickGame,
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

            // Botón "Play Online" con estilo secundario
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

            // Botón de información con animación y tooltip visual
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

            // Mostrar mensaje cuando se activa la información
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

            // Versión pequeña
            Text(
                text = "v1.0.0",
                fontSize = 12.sp,
                color = Color(0xFF4A4A62)
            )
        }
    }
}

