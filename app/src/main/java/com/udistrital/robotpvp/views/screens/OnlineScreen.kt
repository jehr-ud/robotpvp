package com.udistrital.robotpvp.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val GAME_CODE_LENGTH = 5

@Composable
fun OnlineScreen(
    onCreateGame: () -> Unit,
    onJoinGame: (code: String) -> Unit,
    onBack: () -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    val isCodeValid = code.length == GAME_CODE_LENGTH

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1A1A2E)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Jugar Online",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE94560),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa el código de partida de tu oponente",
                fontSize = 16.sp,
                color = Color(0xFFB0B0C0),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo del código: mayúsculas, solo letras/números, máx. 5
            OutlinedTextField(
                value = code,
                onValueChange = { input ->
                    code = input
                        .uppercase()
                        .filter { it.isLetterOrDigit() }
                        .take(GAME_CODE_LENGTH)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Código de partida") },
                placeholder = { Text("Ej: AB12C") },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF4A4A62),
                    focusedLabelColor = Color(0xFF38BDF8),
                    unfocusedLabelColor = Color(0xFF8A8AA8),
                    cursorColor = Color(0xFF38BDF8)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Solo se activa con el código completo
            Button(
                onClick = { onJoinGame(code) },
                enabled = isCodeValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE94560),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF2D2D44),
                    disabledContentColor = Color(0xFF6C6C8A)
                )
            ) {
                Text("Conectarse", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "— o —", color = Color(0xFF6C6C8A), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D2D44),
                    contentColor = Color.White
                )
            ) {
                Text("Crear partida nueva", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onBack) {
                Text("Volver al menú", color = Color(0xFF8A8AA8))
            }
        }
    }
}