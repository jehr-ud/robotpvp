package com.udistrital.robotpvp.composables

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen() {

    val context = LocalContext.current

    var accelX by remember { mutableFloatStateOf(0f) }
    var accelY by remember { mutableFloatStateOf(0f) }
    var accelZ by remember { mutableFloatStateOf(0f) }

    var gyroX by remember { mutableFloatStateOf(0f) }
    var gyroY by remember { mutableFloatStateOf(0f) }
    var gyroZ by remember { mutableFloatStateOf(0f) }


    val sensorManager = remember {
        context.getSystemService(SensorManager::class.java)
    }

    DisposableEffect(sensorManager) {

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

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {
                // TODO: handle it
            }
        }

        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ),
            SensorManager.SENSOR_DELAY_GAME
        )


        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE
            ),
            SensorManager.SENSOR_DELAY_GAME
        )


        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }


    var robot1X by remember { mutableFloatStateOf(250f) }
    var robot1Y by remember { mutableFloatStateOf(400f) }

    var robot2X by remember { mutableFloatStateOf(700f) }
    var robot2Y by remember { mutableFloatStateOf(800f) }



    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            drawRect(
                color = Color(0xFF202124),
                topLeft = Offset.Zero,
                size = size
            )


            drawRect(
                color = Color.White,
                topLeft = Offset(
                    20f,
                    20f
                ),
                size = androidx.compose.ui.geometry.Size(
                    size.width - 40f,
                    size.height - 40f
                ),
                style = Stroke(width = 5f)
            )


            drawCircle(
                color = Color.Red,
                radius = 60f,
                center = Offset(
                    robot1X,
                    robot1Y
                )
            )

            drawCircle(
                color = Color.White,
                radius = 60f,
                center = Offset(
                    robot1X,
                    robot1Y
                ),
                style = Stroke(width = 5f)
            )


            drawCircle(
                color = Color.Cyan,
                radius = 60f,
                center = Offset(
                    robot2X,
                    robot2Y
                )
            )

            drawCircle(
                color = Color.White,
                radius = 60f,
                center = Offset(
                    robot2X,
                    robot2Y
                ),
                style = Stroke(width = 5f)
            )
        }


        Text(
            text = """
                
                ACELERÓMETRO
                
                X: %.2f
                Y: %.2f
                Z: %.2f
                
                GIROSCOPIO
                
                X: %.2f
                Y: %.2f
                Z: %.2f
                
            """.trimIndent().format(
                accelX,
                accelY,
                accelZ,
                gyroX,
                gyroY,
                gyroZ
            ),
            modifier = Modifier.align(
                Alignment.TopStart
            ),
            color = Color.White,
            fontSize = 16.sp
        )
    }
}