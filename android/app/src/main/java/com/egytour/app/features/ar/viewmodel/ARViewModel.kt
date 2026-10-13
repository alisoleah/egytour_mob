package com.egytour.app.features.ar.viewmodel

import androidx.lifecycle.ViewModel
import com.egytour.app.features.explore.domain.POI
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

class ARViewModel : ViewModel() {
    private val _distanceToDestination = MutableStateFlow(185.0)
    val distanceToDestination: StateFlow<Double> = _distanceToDestination.asStateFlow()

    private val _relativeBearing = MutableStateFlow(24.0f) // Degrees relative to phone direction
    val relativeBearing: StateFlow<Float> = _relativeBearing.asStateFlow()

    private val _targetBearing = MutableStateFlow(42.0f) // Absolute azimuth (0 - 360)
    val targetBearing: StateFlow<Float> = _targetBearing.asStateFlow()

    private val _guidanceText = MutableStateFlow("Turn 24° Right")
    val guidanceText: StateFlow<String> = _guidanceText.asStateFlow()

    private val _isFacingDestination = MutableStateFlow(false)
    val isFacingDestination: StateFlow<Boolean> = _isFacingDestination.asStateFlow()

    private val _isARActive = MutableStateFlow(true)
    val isARActive: StateFlow<Boolean> = _isARActive.asStateFlow()

    fun updateNavigation(destination: POI, userLocation: LatLng = LatLng(29.9735, 31.1310), deviceHeading: Float = 0f) {
        val dest = destination.coordinates
        val bearing = calculateBearing(userLocation, dest)
        _targetBearing.value = bearing

        var rel = bearing - deviceHeading
        while (rel > 180f) rel -= 360f
        while (rel < -180f) rel += 360f
        _relativeBearing.value = rel

        val facing = abs(rel) <= 15f
        _isFacingDestination.value = facing
        _guidanceText.value = when {
            facing -> "Directly Ahead"
            rel > 0f -> "Turn ${rel.toInt()}° Right"
            else -> "Turn ${(-rel).toInt()}° Left"
        }

        // Distance formula (Haversine approx)
        val dLat = Math.toRadians(dest.latitude - userLocation.latitude)
        val dLon = Math.toRadians(dest.longitude - userLocation.longitude)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(userLocation.latitude)) * cos(Math.toRadians(dest.latitude)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        _distanceToDestination.value = 6371000 * c
    }

    private fun calculateBearing(from: LatLng, to: LatLng): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x)).toFloat()

        return (bearing + 360f) % 360f
    }

    fun toggleARMode() {
        _isARActive.value = !_isARActive.value
    }
}

