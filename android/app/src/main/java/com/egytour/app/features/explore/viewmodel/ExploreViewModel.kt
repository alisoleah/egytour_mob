package com.egytour.app.features.explore.viewmodel

import androidx.lifecycle.ViewModel
import com.egytour.app.features.explore.domain.POI
import com.egytour.app.features.explore.domain.POIType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        // Mock Data for Sprint 3
        _pois.value = listOf(
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
