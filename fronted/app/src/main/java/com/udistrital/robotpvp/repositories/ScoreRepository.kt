package com.udistrital.robotpvp.repositories

import com.udistrital.robotpvp.data.models.PlayerStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Comunicación con el backend de puntajes (Node.js)
class ScoreRepository {

    companion object {
        // 10.0.2.2 es el localhost del computador visto desde el emulador.
        // En un celular físico, cambia esto por la IP local del PC (ej. http://192.168.1.10:3000)
        const val BASE_URL = "http://10.0.2.2:3000"
        private const val TIMEOUT_MS = 5000
    }

    suspend fun getStats(uid: String): Result<PlayerStats> = withContext(Dispatchers.IO) {
        runCatching {
            val json = request("GET", "$BASE_URL/v1/score/stats/user/$uid")
            val data = json.getJSONObject("data")
            PlayerStats(
                totalGames = data.optInt("total_games"),
                wins = data.optInt("wins"),
                losses = data.optInt("losses"),
                bestScore = data.optInt("best_score")
            )
        }
    }

    suspend fun saveGame(uid: String, score: Int, winner: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject()
                .put("score", score)
                .put("winner", winner)
            request("POST", "$BASE_URL/v1/score/history/user/$uid", body)
            Unit
        }
    }

    private fun request(method: String, url: String, body: JSONObject? = null): JSONObject {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.setRequestProperty("Accept", "application/json")

            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use { it.write(body.toString().toByteArray()) }
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() } ?: "{}"
            val json = JSONObject(text)

            // Si el backend responde con error (ej. 400 por validación), mostramos su mensaje
            if (code !in 200..299) {
                throw Exception(json.optString("message", "Error del servidor ($code)"))
            }
            return json
        } finally {
            connection.disconnect()
        }
    }
}
