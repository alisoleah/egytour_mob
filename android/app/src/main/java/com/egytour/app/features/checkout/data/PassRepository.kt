package com.egytour.app.features.checkout.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * Hotel pass activation — the Tour_os PMS ↔ EgyTour bridge.
 *
 * Guests buy an experience at the hotel front desk (charged to their folio in
 * the PMS) and receive an EG-XXXXXXXX access code. Entering it here activates
 * the pass for this device's account and unlocks the content tier.
 *
 * Networking follows the app's existing idiom (HttpURLConnection + JSONObject).
 */
object PassRepository {

    // 10.0.2.2 is the host loopback from inside the Android emulator.
    private const val BASE_URL = "http://10.0.2.2:3000/api/v1"
    private const val PREFS = "egytour_auth"
    private const val KEY_TOKEN = "access_token"

    data class ActivationResult(
        val success: Boolean,
        val alreadyActive: Boolean = false,
        val experienceName: String? = null,
        val expiresAt: String? = null,
        val error: String? = null,
    )

    data class Pass(
        val id: String,
        val accessCode: String,
        val experienceName: String,
        val expiresAt: String?,
    )

    /**
     * The app has no mandatory login; passes are tied to a lightweight guest
     * account created (and cached) on first activation so reinstalling the
     * app or clearing storage simply starts a new guest account.
     */
    private fun ensureGuestToken(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.getString(KEY_TOKEN, null)?.let { return it }

        val email = "guest-${UUID.randomUUID()}@device.egytour.app"
        val password = UUID.randomUUID().toString()
        val body = JSONObject()
            .put("email", email)
            .put("password", password)
            .put("displayName", "EgyTour Guest")

        val connection = open("$BASE_URL/auth/register", "POST")
        connection.outputStream.use { stream ->
            DataOutputStream(stream).use { it.writeBytes(body.toString()) }
        }
        val status = connection.responseCode
        val payload = readBody(connection, status)

        // Register/login return the access token at the top level.
        val token = payload?.optString("accessToken")
            ?.takeIf { it.isNotEmpty() && it != "null" }
        if (status !in 200..299 || token.isNullOrBlank()) {
            throw IllegalStateException("Could not create guest account (HTTP $status)")
        }
        prefs.edit().putString(KEY_TOKEN, token).apply()
        return token
    }

    /** Activate a hotel-issued EG-XXXXXXXX code for this device's account. */
    suspend fun activatePass(context: Context, code: String): ActivationResult =
        withContext(Dispatchers.IO) {
            try {
                val token = ensureGuestToken(context)
                val body = JSONObject().put("code", code.trim().uppercase())
                val connection = open("$BASE_URL/passes/activate", "POST", token)
                connection.outputStream.use { stream ->
                    DataOutputStream(stream).use { it.writeBytes(body.toString()) }
                }
                val status = connection.responseCode
                val payload = readBody(connection, status)

                when {
                    status in 200..299 && payload != null -> ActivationResult(
                        success = payload.optBoolean("success"),
                        alreadyActive = payload.optBoolean("alreadyActive"),
                        experienceName = payload.optJSONObject("pass")
                            ?.optString("experienceName"),
                        expiresAt = payload.optString("expiresAt").takeIf { it.isNotEmpty() },
                    )
                    payload != null -> ActivationResult(
                        success = false,
                        error = payload.optString("error").ifEmpty { "Activation failed (HTTP $status)" },
                    )
                    else -> ActivationResult(success = false, error = "Activation failed (HTTP $status)")
                }
            } catch (e: Exception) {
                ActivationResult(success = false, error = e.message ?: "Network error")
            }
        }

    /** The activated passes on this account. */
    suspend fun getPasses(context: Context): List<Pass> = withContext(Dispatchers.IO) {
        try {
            val token = ensureGuestToken(context)
            val connection = open("$BASE_URL/passes", "GET", token)
            val status = connection.responseCode
            val payload = readBody(connection, status) ?: return@withContext emptyList()
            val passes = payload.optJSONArray("passes") ?: return@withContext emptyList()
            (0 until passes.length()).mapNotNull { i ->
                val p = passes.optJSONObject(i) ?: return@mapNotNull null
                Pass(
                    id = p.optString("id"),
                    accessCode = p.optString("accessCode"),
                    experienceName = p.optString("experienceName"),
                    expiresAt = p.optString("expiresAt").takeIf { it.isNotEmpty() },
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun open(urlString: String, method: String, token: String? = null): HttpURLConnection {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }
        if (method == "POST") {
            connection.doOutput = true
        }
        return connection
    }

    private fun readBody(connection: HttpURLConnection, status: Int): JSONObject? {
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val text = stream?.bufferedReader()?.use { it.readText() } ?: return null
        connection.disconnect()
        return try {
            JSONObject(text)
        } catch (e: Exception) {
            null
        }
    }
}
