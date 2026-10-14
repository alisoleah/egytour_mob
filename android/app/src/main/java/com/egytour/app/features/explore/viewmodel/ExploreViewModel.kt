package com.egytour.app.features.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egytour.app.features.explore.domain.POI
import com.egytour.app.features.explore.domain.POIType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class RouteStep(
    val instruction: String,
    val distanceMeters: Int,
    val icon: String = "straight"
)

class ExploreViewModel : ViewModel() {
    private val _pois = MutableStateFlow<List<POI>>(emptyList())
    val pois: StateFlow<List<POI>> = _pois.asStateFlow()

    private val _selectedPOI = MutableStateFlow<POI?>(null)
    val selectedPOI: StateFlow<POI?> = _selectedPOI.asStateFlow()

    // Navigation State
    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()

    private val _routeCoordinates = MutableStateFlow<List<LatLng>>(emptyList())
    val routeCoordinates: StateFlow<List<LatLng>> = _routeCoordinates.asStateFlow()

    private val _routeSteps = MutableStateFlow<List<RouteStep>>(emptyList())
    val routeSteps: StateFlow<List<RouteStep>> = _routeSteps.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _totalDistanceMeters = MutableStateFlow(0)
    val totalDistanceMeters: StateFlow<Int> = _totalDistanceMeters.asStateFlow()

    private val _estimatedMinutes = MutableStateFlow(0)
    val estimatedMinutes: StateFlow<Int> = _estimatedMinutes.asStateFlow()

    init {
        loadPOIs()
    }

    fun selectPOI(poi: POI?) {
        _selectedPOI.value = poi
        if (poi == null) {
            exitNavigation()
        }
    }

    fun calculateRoute(destination: POI) {
        val userLocation = LatLng(29.9735, 31.1310) // Giza visitor entrance
        val destLocation = destination.coordinates

        // Synthesize pedestrian walking path
        val mid1 = LatLng(
            userLocation.latitude + (destLocation.latitude - userLocation.latitude) * 0.35 + 0.0003,
            userLocation.longitude + (destLocation.longitude - userLocation.longitude) * 0.35
        )
        val mid2 = LatLng(
            userLocation.latitude + (destLocation.latitude - userLocation.latitude) * 0.70,
            userLocation.longitude + (destLocation.longitude - userLocation.longitude) * 0.70 + 0.0003
        )

        _routeCoordinates.value = listOf(userLocation, mid1, mid2, destLocation)

        val approxDistance = 420
        _totalDistanceMeters.value = approxDistance
        _estimatedMinutes.value = maxOf(1, approxDistance / 75)

        _routeSteps.value = listOf(
            RouteStep("Head north along Pyramid Entrance Pathway", 120, "straight"),
            RouteStep("Turn right at the Eastern Causeway", 180, "right"),
            RouteStep("Continue straight toward monument plateau", 100, "straight"),
            RouteStep("Arrive at ${destination.name}", 20, "arrive")
        )
        _currentStepIndex.value = 0
    }

    fun startNavigation() {
        _isNavigating.value = true
    }

    fun exitNavigation() {
        _isNavigating.value = false
        _routeCoordinates.value = emptyList()
        _routeSteps.value = emptyList()
        _currentStepIndex.value = 0
    }

    fun nextStep() {
        if (_currentStepIndex.value < _routeSteps.value.size - 1) {
            _currentStepIndex.value += 1
        }
    }

    private fun loadPOIs() {
        // Initial offline-ready POIs
        _pois.value = getFallbackPOIs()

        // Fetch live POIs from Fastify PostGIS backend
        viewModelScope.launch {
            val livePois = fetchLivePOIs(29.9792, 31.1342)
            if (livePois.isNotEmpty()) {
                _pois.value = livePois
            }
        }
    }

    private suspend fun fetchLivePOIs(lat: Double, lon: Double): List<POI> = withContext(Dispatchers.IO) {
        val endpoints = listOf(
            "http://10.0.2.2:3000/api/v1/pois/nearby?lat=$lat&lon=$lon&radius=5000",
            "http://localhost:3000/api/v1/pois/nearby?lat=$lat&lon=$lon&radius=5000"
        )
        for (urlString in endpoints) {
            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 4000
                conn.readTimeout = 4000

                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    val poisArray = json.getJSONArray("pois")
                    val result = mutableListOf<POI>()

                    for (i in 0 until poisArray.length()) {
                        val item = poisArray.getJSONObject(i)
                        val id = item.getString("id")
                        val nameEn = item.getString("nameEn")
                        val rawType = item.optString("poiType", "monument").lowercase()
                        val type = when (rawType) {
                            "pyramid" -> POIType.PYRAMID
                            "temple" -> POIType.TEMPLE
                            "museum" -> POIType.MUSEUM
                            "market" -> POIType.MARKET
                            else -> POIType.MODERN
                        }

                        val coordsObj = item.getJSONObject("coordinates")
                        val latitude = coordsObj.getDouble("latitude")
                        val longitude = coordsObj.getDouble("longitude")
                        val radius = item.optDouble("triggerRadiusMeters", 50.0)
                        val period = item.optString("historicalPeriod", "Old Kingdom")
                        val img = item.optString("imageUrl", "great_pyramid")

                        result.add(
                            POI(
                                id = id,
                                name = nameEn,
                                description = "Historic $rawType at Giza ($period).",
                                type = type,
                                coordinates = LatLng(latitude, longitude),
                                imageUrl = img,
                                audioUrl = "https://cdn.egytour.app/audio/$id/en/casual.mp3",
                                historicalPeriod = period.replace("_", " ").replaceFirstChar { it.uppercase() },
                                triggerRadius = radius
                            )
                        )
                    }

                    if (result.isNotEmpty()) {
                        return@withContext result
                    }
                }
            } catch (_: Exception) {
                // Try next endpoint
            }
        }
        emptyList()
    }

    private fun getFallbackPOIs(): List<POI> {
        return listOf(
            POI(
                id = "giza-1",
                name = "Great Pyramid of Giza",
                description = "The largest Egyptian pyramid and tomb of Fourth Dynasty pharaoh Khufu.",
                type = POIType.PYRAMID,
                coordinates = LatLng(29.9792, 31.1342),
                imageUrl = "great_pyramid",
                audioUrl = "https://example.com/audio/giza.mp3",
                historicalPeriod = "Old Kingdom",
                triggerRadius = 100.0
            ),
            POI(
                id = "giza-2",
                name = "Great Sphinx",
                description = "Limestone statue of a reclining sphinx, a mythical creature.",
                type = POIType.TEMPLE,
                coordinates = LatLng(29.9753, 31.1376),
                imageUrl = "sphinx",
                audioUrl = "https://example.com/audio/sphinx.mp3",
                historicalPeriod = "Old Kingdom",
                triggerRadius = 50.0
            ),
            POI(
                id = "luxor-1",
                name = "Luxor Temple",
                description = "Large Ancient Egyptian temple complex located on the east bank of the Nile River.",
                type = POIType.TEMPLE,
                coordinates = LatLng(25.7000, 32.6398),
                imageUrl = "luxor_temple",
                audioUrl = null,
                historicalPeriod = "New Kingdom",
                triggerRadius = 150.0
            )
        )
    }
}
