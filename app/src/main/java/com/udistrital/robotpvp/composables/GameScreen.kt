package com.udistrital.robotpvp.composables

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.robotpvp.enums.GameWinner
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun GameScreen(
    gameDurationSeconds: Float = 30f,
    onBackToMenu: () -> Unit = {}
) {
    val context = LocalContext.current

    // Estado del juego
    var isGameOver by remember { mutableStateOf(false) }
    var timeRemaining by remember(gameDurationSeconds) { mutableFloatStateOf(gameDurationSeconds) }
    var winner by remember { mutableStateOf<GameWinner?>(null) }

    var accelX by remember { mutableFloatStateOf(0f) }
    var accelY by remember { mutableFloatStateOf(0f) }
    var accelZ by remember { mutableFloatStateOf(0f) }

    var gyroX by remember { mutableFloatStateOf(0f) }
    var gyroY by remember { mutableFloatStateOf(0f) }
    var gyroZ by remember { mutableFloatStateOf(0f) }

    // Estado físico del Robot 1 (Jugador)
    var robot1Pos by remember { mutableStateOf(Offset(300f, 400f)) }
    var robot1VelX by remember { mutableFloatStateOf(0f) }
    var robot1VelY by remember { mutableFloatStateOf(0f) }

    // Estado del Robot 2 (Enemigo)
    var robot2Pos by remember { mutableStateOf(Offset(0f, 0f)) }
    var targetBorderPos by remember { mutableStateOf(Offset(0f, 0f)) }

    // Configuración Física
    val robotRadius = 60f
    val borderWidth = 20f
    val elasticity = 0.75f
    val friction = 0.985f
    val blueSpeed = 12f

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // Función auxiliar para obtener un punto aleatorio estrictamente en el borde
    fun getRandomScreenPosition(minX: Float, maxX: Float, minY: Float, maxY: Float): Offset {
        val x = Random.nextFloat() * (maxX - minX) + minX
        val y = Random.nextFloat() * (maxY - minY) + minY
        return Offset(x, y)
    }

    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }

    DisposableEffect(sensorManager) {
        if (sensorManager == null) return@DisposableEffect onDispose {}

        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelX = event.values[0]
                        accelY = event.values[1]
                        accelZ = event.values[2]
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gyroX = event.values[0]
                        gyroY = event.values[1]
                        gyroZ = event.values[2]
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelSensor?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME) }
        gyroSensor?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME) }

        onDispose { sensorManager.unregisterListener(sensorListener) }
    }

    // Loop de Física y Temporizador
    LaunchedEffect(canvasSize, isGameOver) {
        if (canvasSize == Size.Zero || isGameOver) return@LaunchedEffect

        val minX = borderWidth + robotRadius
        val maxX = canvasSize.width - borderWidth - robotRadius
        val minY = borderWidth + robotRadius
        val maxY = canvasSize.height - borderWidth - robotRadius

        // Inicializar posición de la bola azul en un lugar aleatorio
        if (robot2Pos == Offset.Zero) {
            robot2Pos = getRandomScreenPosition(minX, maxX, minY, maxY)
            targetBorderPos = getRandomScreenPosition(minX, maxX, minY, maxY)
        }

        var lastFrameTime = 0L

        while (!isGameOver) {
            withFrameNanos { frameTimeNanos ->
                val dt = if (lastFrameTime == 0L) 0f else (frameTimeNanos - lastFrameTime) / 1_000_000_000f
                lastFrameTime = frameTimeNanos

                // Actualizar temporizador de 30s hacia atrás
                timeRemaining = (timeRemaining - dt).coerceAtLeast(0f)

                if (timeRemaining <= 0f) {
                    winner = GameWinner.PLAYER
                    isGameOver = true
                    return@withFrameNanos
                }

                // 1. Sensibilidad para la Bola Roja
                val accelSensitivity = 0.15f
                robot1VelX += -accelX * accelSensitivity
                robot1VelY += accelY * accelSensitivity

                robot1VelX *= friction
                robot1VelY *= friction

                var nextX = robot1Pos.x + robot1VelX
                var nextY = robot1Pos.y + robot1VelY

                // Colisiones Bola Roja con paredes
                if (nextX <= minX) { nextX = minX; robot1VelX = -robot1VelX * elasticity }
                else if (nextX >= maxX) { nextX = maxX; robot1VelX = -robot1VelX * elasticity }

                if (nextY <= minY) { nextY = minY; robot1VelY = -robot1VelY * elasticity }
                else if (nextY >= maxY) { nextY = maxY; robot1VelY = -robot1VelY * elasticity }

                robot1Pos = Offset(nextX, nextY)

                // 2. Movimiento de la Bola Azul a cualquier punto al azar
                val dx = targetBorderPos.x - robot2Pos.x
                val dy = targetBorderPos.y - robot2Pos.y
                val distanceToTarget = hypot(dx, dy)

                if (distanceToTarget < blueSpeed) {
                    robot2Pos = targetBorderPos
                    targetBorderPos = getRandomScreenPosition(minX, maxX, minY, maxY)
                } else {
                    val vx = (dx / distanceToTarget) * blueSpeed
                    val vy = (dy / distanceToTarget) * blueSpeed
                    robot2Pos = Offset(robot2Pos.x + vx, robot2Pos.y + vy)
                }

                // DETECCIÓN DE COLISIÓN ENTRE BOLAS
                val distanceBetweenRobots = hypot(robot1Pos.x - robot2Pos.x, robot1Pos.y - robot2Pos.y)
                if (distanceBetweenRobots <= robotRadius * 2) {
                    winner = GameWinner.MACHINE
                    isGameOver = true
                }
            }
        }
    }

    // Renderizado de la UI
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (canvasSize != size) {
                canvasSize = size
            }

            // 1. FONDO SCI-FI
            val backgroundColor = Color(0xFF0B0F19)
            drawRect(color = backgroundColor, topLeft = Offset.Zero, size = size)

            // 2. CUADRÍCULA DE ARENA
            val gridColor = Color(0xFF1E293B)
            val gridSize = 100f
            for (x in 0..(size.width / gridSize).toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(x * gridSize, 0f),
                    end = Offset(x * gridSize, size.height),
                    strokeWidth = 2f
                )
            }
            for (y in 0..(size.height / gridSize).toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y * gridSize),
                    end = Offset(size.width, y * gridSize),
                    strokeWidth = 2f
                )
            }

            // 3. BORDE DE ARENA (Neón brillante)
            drawRect(
                color = Color(0xFF38BDF8),
                topLeft = Offset(borderWidth, borderWidth),
                size = Size(size.width - (borderWidth * 2), size.height - (borderWidth * 2)),
                style = Stroke(width = 8f)
            )

            // Calculamos el tamaño total del robot a dibujar basado en tu radio de colisión
            val robotSizePx = robotRadius * 2f

            // 4. Robot 1 (Jugador) - Color Verde Neón
            drawRobot(
                x = robot1Pos.x,
                y = robot1Pos.y,
                primaryColor = Color(0xFF10B981),
                eyeColor = Color(0xFFA7F3D0),
                sizePx = robotSizePx
            )

            // 5. Robot 2 (Enemigo) - Color Rojo Neón
            drawRobot(
                x = robot2Pos.x,
                y = robot2Pos.y,
                primaryColor = Color(0xFFEF4444),
                eyeColor = Color(0xFFFECACA),
                sizePx = robotSizePx
            )
        }

        // HUD - Telemetría
        Text(
            text = """
                SYS_ACCEL
                X: %.2f | Y: %.2f | Z: %.2f
                
                VELOCIDAD JUGADOR
                Vx: %.1f | Vy: %.1f
            """.trimIndent().format(accelX, accelY, accelZ, robot1VelX, robot1VelY),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(28.dp),
            color = Color(0xFF38BDF8), // Texto azul cibernético
            fontSize = 13.sp
        )

        // Temporizador en la parte inferior de la pantalla
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
                .background(
                    color = Color(0xFF1E293B).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (timeRemaining <= 5f) Color(0xFFEF4444) else Color(0xFF38BDF8),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TIEMPO RESTANTE",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                val seconds = ceil(timeRemaining).toInt().coerceAtLeast(0)
                Text(
                    text = String.format(Locale.US, "%02d s", seconds),
                    color = if (timeRemaining <= 5f) Color(0xFFEF4444) else Color(0xFF38BDF8),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                LinearProgressIndicator(
                    progress = { (timeRemaining / gameDurationSeconds).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .width(140.dp)
                        .height(6.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (timeRemaining <= 5f) Color(0xFFEF4444) else Color(0xFF38BDF8),
                    trackColor = Color(0xFF334155)
                )
            }
        }

        // Pantalla de Fin de Juego (Game Over / Victoria)
        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (winner) {
                        GameWinner.PLAYER -> {
                            Text(
                                text = "¡VICTORIA!",
                                color = Color(0xFF10B981), // Verde Neón
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "GANA EL JUGADOR",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Sobreviviste los ${gameDurationSeconds.toInt()} segundos sin colisionar",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        }
                        GameWinner.MACHINE -> {
                            Text(
                                text = "¡DERROTA!",
                                color = Color(0xFFEF4444), // Rojo Neón
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "GANA LA MÁQUINA",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Colisión detectada antes de agotar el tiempo",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        }
                        null -> {}
                    }

                    Button(
                        onClick = {
                            // Reiniciar Variables del Juego
                            robot1Pos = Offset(300f, 400f)
                            robot1VelX = 0f
                            robot1VelY = 0f
                            robot2Pos = Offset.Zero
                            targetBorderPos = Offset.Zero
                            timeRemaining = gameDurationSeconds
                            winner = null
                            isGameOver = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38BDF8),
                            contentColor = Color(0xFF0B0F19)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "REINICIAR SISTEMA",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onBackToMenu,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF94A3B8)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF475569))
                    ) {
                        Text(text = "VOLVER AL MENÚ PRINCIPAL")
                    }
                }
            }
        }
    }
}

/**
 * Función auxiliar para dibujar un robot usando formas geométricas.
 */
fun DrawScope.drawRobot(x: Float, y: Float, primaryColor: Color, eyeColor: Color, sizePx: Float) {
    val halfSize = sizePx / 2f
    val trackWidth = sizePx * 0.2f

    // Mueve el pincel al centro exacto
    translate(left = x - halfSize, top = y - halfSize) {

        // 1. Llantas (Grises)
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(0f, sizePx * 0.2f),
            size = Size(trackWidth, sizePx * 0.7f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(sizePx - trackWidth, sizePx * 0.2f),
            size = Size(trackWidth, sizePx * 0.7f),
            cornerRadius = CornerRadius(10f, 10f)
        )

        // 2. Antena
        drawLine(
            color = Color.Gray,
            start = Offset(sizePx / 2, 0f),
            end = Offset(sizePx / 2, sizePx * 0.2f),
            strokeWidth = 4f
        )
        drawCircle(
            color = primaryColor,
            radius = sizePx * 0.05f,
            center = Offset(sizePx / 2, 0f)
        )

        // 3. Cuerpo principal
        drawRoundRect(
            color = primaryColor,
            topLeft = Offset(sizePx * 0.15f, sizePx * 0.3f),
            size = Size(sizePx * 0.7f, sizePx * 0.6f),
            cornerRadius = CornerRadius(15f, 15f)
        )

        // 4. Cabeza / Pantalla
        val headWidth = sizePx * 0.5f
        drawRoundRect(
            color = Color(0xFF1E293B), // Pantalla oscura
            topLeft = Offset(sizePx / 2 - headWidth / 2, sizePx * 0.1f),
            size = Size(headWidth, sizePx * 0.3f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // 5. Ojos
        val eyeRadius = sizePx * 0.06f
        val eyeY = sizePx * 0.25f
        drawCircle(
            color = eyeColor,
            radius = eyeRadius,
            center = Offset(sizePx * 0.4f, eyeY)
        )
        drawCircle(
            color = eyeColor,
            radius = eyeRadius,
            center = Offset(sizePx * 0.6f, eyeY)
        )

        // 6. Detalle del núcleo
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = sizePx * 0.1f,
            center = Offset(sizePx / 2, sizePx * 0.65f)
        )
    }
}
