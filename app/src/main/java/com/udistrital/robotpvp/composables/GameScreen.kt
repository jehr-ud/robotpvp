package com.udistrital.robotpvp.composables

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun GameScreen() {
    val context = LocalContext.current

    // Estado del juego
    var isGameOver by remember { mutableStateOf(false) }


    var accelX by remember { mutableFloatStateOf(0f) }
    var accelY by remember { mutableFloatStateOf(0f) }
    var accelZ by remember { mutableFloatStateOf(0f) }

    var gyroX by remember { mutableFloatStateOf(0f) }
    var gyroY by remember { mutableFloatStateOf(0f) }
    var gyroZ by remember { mutableFloatStateOf(0f) }

    // Estado físico del Robot 1
    var robot1Pos by remember { mutableStateOf(Offset(300f, 400f)) }
    var robot1VelX by remember { mutableFloatStateOf(0f) }
    var robot1VelY by remember { mutableFloatStateOf(0f) }

    // Estado del Robot 2
    var robot2Pos by remember { mutableStateOf(Offset(0f, 0f)) }
    var targetBorderPos by remember { mutableStateOf(Offset(0f, 0f)) }

    // Configuración  Física
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

    // 2. Loop de Física
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

        while (!isGameOver) {
            withFrameNanos {
                // 1. Sensibilidad reducida para frenar la Bola Roja
                val accelSensitivity = 0.15f // Reducido de 0.6f a 0.15f para menor velocidad
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

                // 2. Movimiento de la Bola Azul a cualquier punto al azar de la pantalla
                val dx = targetBorderPos.x - robot2Pos.x
                val dy = targetBorderPos.y - robot2Pos.y
                val distanceToTarget = hypot(dx, dy)

                // Si llegó al destino actual, seleccionar un nuevo destino en cualquier parte de la pantalla
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
                    isGameOver = true
                }
            }
        }
    }

    // 3. Renderizado de la UI
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (canvasSize != size) {
                canvasSize = size
            }

            // Fondo
            drawRect(color = Color(0xFF202124), topLeft = Offset.Zero, size = size)

            // Borde / Paredes
            drawRect(
                color = Color.White,
                topLeft = Offset(borderWidth, borderWidth),
                size = Size(size.width - (borderWidth * 2), size.height - (borderWidth * 2)),
                style = Stroke(width = 5f)
            )

            // Robot 1 (Rojo)
            drawCircle(color = Color.Red, radius = robotRadius, center = robot1Pos)
            drawCircle(color = Color.White, radius = robotRadius, center = robot1Pos, style = Stroke(width = 5f))

            // Robot 2 (Azul)
            drawCircle(color = Color.Cyan, radius = robotRadius, center = robot2Pos)
            drawCircle(color = Color.White, radius = robotRadius, center = robot2Pos, style = Stroke(width = 5f))
        }


        Text(
            text = """
                ACELERÓMETRO
                X: %.2f | Y: %.2f | Z: %.2f
                
                VELOCIDAD ROJA
                Vx: %.1f | Vy: %.1f
            """.trimIndent().format(accelX, accelY, accelZ, robot1VelX, robot1VelY),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(28.dp),
            color = Color.White,
            fontSize = 13.sp
        )


        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡JUEGO TERMINADO!",
                        color = Color.Red,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(onClick = {
                        // Reiniciar Variables del Juego
                        robot1Pos = Offset(300f, 400f)
                        robot1VelX = 0f
                        robot1VelY = 0f
                        robot2Pos = Offset(borderWidth + robotRadius, borderWidth + robotRadius)
                        isGameOver = false
                    }) {
                        Text(text = "Reiniciar Juego")
                    }
                }
            }
        }
    }
}