package com.egytour.app.features.itinerary.domain

data class ItineraryStop(
    val poiId: String? = null,
    val title: String,
    val region: String,
    val orderIndex: Int,
    val recommendedDurationMinutes: Int = 90,
    val notes: String = ""
)

data class ItineraryDay(
    val dayNumber: Int,
    val theme: String,
    val region: String,
    val stops: List<ItineraryStop>
)

data class Itinerary(
    val id: String,
    val title: String,
    val durationDays: Int,
    val regions: List<String>,
    val days: List<ItineraryDay>,
    val createdAt: String
)
