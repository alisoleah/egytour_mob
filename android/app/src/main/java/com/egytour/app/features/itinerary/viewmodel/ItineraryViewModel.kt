package com.egytour.app.features.itinerary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egytour.app.features.itinerary.domain.Itinerary
import com.egytour.app.features.itinerary.domain.ItineraryDay
import com.egytour.app.features.itinerary.domain.ItineraryStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ItineraryViewModel : ViewModel() {
    private val _itineraries = MutableStateFlow<List<Itinerary>>(emptyList())
    val itineraries: StateFlow<List<Itinerary>> = _itineraries.asStateFlow()

    private val _selectedItinerary = MutableStateFlow<Itinerary?>(null)
    val selectedItinerary: StateFlow<Itinerary?> = _selectedItinerary.asStateFlow()

    private val _selectedDayNumber = MutableStateFlow(1)
    val selectedDayNumber: StateFlow<Int> = _selectedDayNumber.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _showGeneratorDialog = MutableStateFlow(false)
    val showGeneratorDialog: StateFlow<Boolean> = _showGeneratorDialog.asStateFlow()

    // Generator Form State
    private val _durationDays = MutableStateFlow(3)
    val durationDays: StateFlow<Int> = _durationDays.asStateFlow()

    private val _selectedRegions = MutableStateFlow(setOf("cairo", "luxor"))
    val selectedRegions: StateFlow<Set<String>> = _selectedRegions.asStateFlow()

    private val _selectedInterests = MutableStateFlow(setOf("ancient_history", "architecture"))
    val selectedInterests: StateFlow<Set<String>> = _selectedInterests.asStateFlow()

    init {
        loadInitialItineraries()
    }

    private fun loadInitialItineraries() {
        if (_itineraries.value.isEmpty()) {
            val sample = generateFallbackItinerary(3, listOf("cairo", "luxor"))
            _itineraries.value = listOf(sample)
        }
    }

    fun selectItinerary(itinerary: Itinerary?) {
        _selectedItinerary.value = itinerary
        _selectedDayNumber.value = 1
    }

    fun selectDay(dayNumber: Int) {
        _selectedDayNumber.value = dayNumber
    }

    fun setShowGeneratorDialog(show: Boolean) {
        _showGeneratorDialog.value = show
    }

    fun setDuration(days: Int) {
        if (days in 1..14) {
            _durationDays.value = days
        }
    }

    fun toggleRegion(region: String) {
        val current = _selectedRegions.value.toMutableSet()
        if (current.contains(region)) {
            if (current.size > 1) {
                current.remove(region)
            }
        } else {
            current.add(region)
        }
        _selectedRegions.value = current
    }

    fun toggleInterest(interest: String) {
        val current = _selectedInterests.value.toMutableSet()
        if (current.contains(interest)) {
            current.remove(interest)
        } else {
            current.add(interest)
        }
        _selectedInterests.value = current
    }

    fun generateItinerary(onComplete: (Boolean) -> Unit = {}) {
        _isGenerating.value = true
        val days = _durationDays.value
        val regions = _selectedRegions.value.toList()
        val interests = _selectedInterests.value.toList()

        viewModelScope.launch {
            try {
                val generated = fetchFromApi(days, regions, interests)
                if (generated != null) {
                    _itineraries.value = listOf(generated) + _itineraries.value
                    _selectedItinerary.value = generated
                    _selectedDayNumber.value = 1
                    _showGeneratorDialog.value = false
                    onComplete(true)
                } else {
                    val fallback = generateFallbackItinerary(days, regions)
                    _itineraries.value = listOf(fallback) + _itineraries.value
                    _selectedItinerary.value = fallback
                    _selectedDayNumber.value = 1
                    _showGeneratorDialog.value = false
                    onComplete(true)
                }
            } catch (e: Exception) {
                val fallback = generateFallbackItinerary(days, regions)
                _itineraries.value = listOf(fallback) + _itineraries.value
                _selectedItinerary.value = fallback
                _selectedDayNumber.value = 1
                _showGeneratorDialog.value = false
                onComplete(true)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private suspend fun fetchFromApi(
        durationDays: Int,
        regions: List<String>,
        interests: List<String>
    ): Itinerary? = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:3000/api/v1/itineraries/generate")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 7000
            conn.readTimeout = 10000
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("durationDays", durationDays)
                put("regions", JSONArray(regions))
                put("interests", JSONArray(interests))
            }

            OutputStreamWriter(conn.outputStream).use { it.write(jsonBody.toString()) }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(responseText)
                val itinObj = root.getJSONObject("itinerary")
                val title = itinObj.getString("title")
                val dCount = itinObj.optInt("durationDays", durationDays)

                val regList = mutableListOf<String>()
                if (itinObj.has("regions")) {
                    val regArr = itinObj.getJSONArray("regions")
                    for (i in 0 until regArr.length()) {
                        regList.add(regArr.getString(i))
                    }
                }

                val dayList = mutableListOf<ItineraryDay>()
                if (itinObj.has("days")) {
                    val dayArr = itinObj.getJSONArray("days")
                    for (i in 0 until dayArr.length()) {
                        val d = dayArr.getJSONObject(i)
                        val dNum = d.getInt("dayNumber")
                        val theme = d.getString("theme")
                        val region = d.getString("region")

                        val stopList = mutableListOf<ItineraryStop>()
                        if (d.has("stops")) {
                            val stopArr = d.getJSONArray("stops")
                            for (j in 0 until stopArr.length()) {
                                val s = stopArr.getJSONObject(j)
                                stopList.add(
                                    ItineraryStop(
                                        poiId = s.optString("poiId", null),
                                        title = s.getString("title"),
                                        region = s.optString("region", region),
                                        orderIndex = j,
                                        recommendedDurationMinutes = s.optInt("recommendedDurationMinutes", 90),
                                        notes = s.optString("notes", "")
                                    )
                                )
                            }
                        }
                        dayList.add(ItineraryDay(dNum, theme, region, stopList))
                    }
                }

                return@withContext Itinerary(
                    id = "itin-${UUID.randomUUID().toString().take(6)}",
                    title = title,
                    durationDays = dCount,
                    regions = if (regList.isEmpty()) regions else regList,
                    days = dayList,
                    createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
                )
            }
        } catch (_: Exception) {}
        null
    }

    private fun generateFallbackItinerary(days: Int, regions: List<String>): Itinerary {
        val dayList = mutableListOf<ItineraryDay>()

        // Day 1
        dayList.add(
            ItineraryDay(
                dayNumber = 1,
                theme = "Giza Plateau: Pyramids & The Sphinx",
                region = "cairo",
                stops = listOf(
                    ItineraryStop(
                        poiId = "giza-1",
                        title = "Great Pyramid of Khufu",
                        region = "cairo",
                        orderIndex = 0,
                        recommendedDurationMinutes = 150,
                        notes = "Morning exploration of the sole surviving Ancient Wonder of the World."
                    ),
                    ItineraryStop(
                        poiId = "giza-2",
                        title = "The Great Sphinx & Valley Temple",
                        region = "cairo",
                        orderIndex = 1,
                        recommendedDurationMinutes = 90,
                        notes = "View the 73-meter limestone guardian and Thutmose IV's Dream Stele."
                    ),
                    ItineraryStop(
                        title = "Grand Egyptian Museum (GEM)",
                        region = "cairo",
                        orderIndex = 2,
                        recommendedDurationMinutes = 180,
                        notes = "Marvel at the full Tutankhamun collection and the Grand Staircase."
                    )
                )
            )
        )

        // Day 2
        if (days >= 2) {
            dayList.add(
                ItineraryDay(
                    dayNumber = 2,
                    theme = "Saqqara Step Pyramid & Medieval Cairo",
                    region = "cairo",
                    stops = listOf(
                        ItineraryStop(
                            title = "Djoser Step Pyramid at Saqqara",
                            region = "cairo",
                            orderIndex = 0,
                            recommendedDurationMinutes = 120,
                            notes = "Visit the world's oldest monumental stone building designed by Imhotep."
                        ),
                        ItineraryStop(
                            title = "Khan el-Khalili Bazaar",
                            region = "cairo",
                            orderIndex = 1,
                            recommendedDurationMinutes = 120,
                            notes = "Wander the 14th-century souk and historic spice markets."
                        )
                    )
                )
            )
        }

        // Day 3
        if (days >= 3) {
            dayList.add(
                ItineraryDay(
                    dayNumber = 3,
                    theme = "Thebes East Bank: Temples of Karnak & Luxor",
                    region = "luxor",
                    stops = listOf(
                        ItineraryStop(
                            title = "Karnak Temple Complex",
                            region = "luxor",
                            orderIndex = 0,
                            recommendedDurationMinutes = 180,
                            notes = "Walk among 134 towering papyrus columns in the largest religious complex ever built."
                        ),
                        ItineraryStop(
                            poiId = "luxor-1",
                            title = "Luxor Temple by Sunset",
                            region = "luxor",
                            orderIndex = 1,
                            recommendedDurationMinutes = 120,
                            notes = "Witness illuminated statues of Ramesses II and the Avenue of Sphinxes."
                        )
                    )
                )
            )
        }

        return Itinerary(
            id = "itin-${UUID.randomUUID().toString().take(6)}",
            title = "$days-Day Classical Egyptian Odyssey",
            durationDays = days,
            regions = if (regions.isEmpty()) listOf("cairo", "luxor") else regions,
            days = dayList,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        )
    }
}
