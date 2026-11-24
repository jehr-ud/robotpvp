package com.udistrital.robotpvp.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.robotpvp.viewmodels.StatsViewModel

@Composable
fun StatsScreen(statsViewModel: StatsViewModel, onBack: () -> Unit) {
    val stats by statsViewModel.stats.collectAsState()
    val isLoading by statsViewModel.isLoading.collectAsState()
    val error by statsViewModel.error.collectAsState()

    // Cada vez que se abre la pantalla se consulta el backend
    LaunchedEffect(Unit) { statsViewModel.loadStats() }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1A1A2E)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mis estadísticas",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE94560)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when {
                isLoading -> CircularProgressIndicator(color = Color(0xFF38BDF8))

                error != null -> {
                    Text(
                        text = error ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { statsViewModel.loadStats() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) { Text("Reintentar", color = Color(0xFF0B0F19)) }
                }

                stats != null -> {
                    val s = stats!!
                    StatRow("Partidas jugadas", s.totalGames.toString(), Color.White)
                    StatRow("Victorias", s.wins.toString(), Color(0xFF10B981))
                    StatRow("Derrotas", s.losses.toString(), Color(0xFFEF4444))
                    StatRow("Mejor puntaje", "${s.bestScore} s", Color(0xFF38BDF8))

                    if (s.totalGames == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aún no tienes partidas. ¡Juega una!",
                            color = Color(0xFF8A8AA8),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            TextButton(onClick = onBack) {
                Text(text = "Volver", color = Color(0xFFB0B0C0), fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D44),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Color(0xFFB0B0C0), fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text(text = value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}
