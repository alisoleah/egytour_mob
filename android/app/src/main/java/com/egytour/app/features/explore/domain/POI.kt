package com.egytour.app.features.explore.domain

import com.google.android.gms.maps.model.LatLng

data class POI(
    val id: String,
    val name: String,
    val description: String?,
    val type: POIType,
    val coordinates: LatLng,
    val imageUrl: String?,
    val audioUrl: String?,
    val historicalPeriod: String?,
    val triggerRadius: Double
)

enum class POIType {
    PYRAMID, TEMPLE, MUSEUM, MARKET, MODERN
}
