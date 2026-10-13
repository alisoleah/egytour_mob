package com.egytour.app.features.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egytour.app.features.ai.domain.ChatMessage
import com.egytour.app.features.ai.domain.ChatSource
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

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _suggestedPrompts = MutableStateFlow(
        listOf(
            "Who actually built the Great Pyramid of Giza?",
            "What is the true story of the Sphinx's missing nose?",
            "What mysteries were found in King Tut's tomb?",
            "How did the Opet Festival connect Luxor and Karnak?"
        )
    )
    val suggestedPrompts: StateFlow<List<String>> = _suggestedPrompts.asStateFlow()

    init {
        seedInitialGreeting()
    }

    private fun seedInitialGreeting() {
        val welcome = ChatMessage(
            text = "Ahlan wa sahlan! I am Dr. Zahi, your dedicated Chief Egyptologist guide. Ask me anything about the pharaohs, ancient pyramids, gods, or archaeological discoveries across Egypt!",
            isUser = false,
            sources = listOf(
                ChatSource("EgyTour Verified Egyptology Knowledge Base", "All Dynasties", 0.99)
            ),
            suggestedFollowUps = listOf(
                "Were the pyramids built by enslaved workers?",
                "What is inside the King's Chamber?",
                "What does the Dream Stele say?"
            )
        )
        _messages.value = listOf(welcome)
    }

    fun updateInputText(newText: String) {
        _inputText.value = newText
    }

    fun sendMessage(customQuery: String? = null, poiId: String? = null) {
        val question = (customQuery ?: _inputText.value).trim()
        if (question.isEmpty()) return

        val userMessage = ChatMessage(text = question, isUser = true)
        _messages.value = _messages.value + userMessage

        if (customQuery == null) {
            _inputText.value = ""
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = fetchAIResponse(question, poiId)
                if (response != null) {
                    _messages.value = _messages.value + response
                } else {
                    _messages.value = _messages.value + generateOfflineFallback(question, poiId)
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + generateOfflineFallback(question, poiId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchAIResponse(question: String, poiId: String?): ChatMessage? = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:3000/api/v1/ai/ask") // 10.0.2.2 is localhost on Android emulator
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 5000
            conn.readTimeout = 7000
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("question", question)
                if (poiId != null) put("poiId", poiId)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(jsonBody.toString()) }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(responseText)
                val answer = jsonObj.getString("answer")

                val sourcesList = mutableListOf<ChatSource>()
                if (jsonObj.has("sources")) {
                    val arr = jsonObj.getJSONArray("sources")
                    for (i in 0 until arr.length()) {
                        val s = arr.getJSONObject(i)
                        sourcesList.add(
                            ChatSource(
                                title = s.optString("title", "Historical Source"),
                                period = s.optString("period", null),
                                confidence = s.optDouble("confidence", 0.95)
                            )
                        )
                    }
                }

                val followUps = mutableListOf<String>()
                if (jsonObj.has("suggestedQuestions")) {
                    val arr = jsonObj.getJSONArray("suggestedQuestions")
                    for (i in 0 until arr.length()) {
                        followUps.add(arr.getString(i))
                    }
                }

                val relatedPoi = jsonObj.optString("relatedPoiId", null)

                return@withContext ChatMessage(
                    text = answer,
                    isUser = false,
                    sources = sourcesList,
                    suggestedFollowUps = followUps,
                    relatedPoiId = relatedPoi
                )
            }
        } catch (_: Exception) {}
        null
    }

    private fun generateOfflineFallback(question: String, poiId: String?): ChatMessage {
        val q = question.lowercase()
        return when {
            q.contains("worker") || q.contains("slave") || q.contains("pyramid") || q.contains("build") -> {
                ChatMessage(
                    text = "The Great Pyramid of Khufu (c. 2560 BCE) was built by an organized workforce of roughly 20,000–30,000 skilled Egyptian craftsmen and seasonal workers, not enslaved people.\n\nExcavations at the Giza Workers' Village revealed bakeries, medical treatment for broken bones, and nearby honorable tombs for workers who died during construction.",
                    isUser = false,
                    sources = listOf(
                        ChatSource("Giza Workers' Village Excavations (Hawass & Lehner)", "4th Dynasty", 0.98),
                        ChatSource("Supreme Council of Antiquities Field Records", "Old Kingdom", 0.95)
                    ),
                    suggestedFollowUps = listOf(
                        "What did the pyramid builders eat?",
                        "How were the 50-ton granite blocks moved?",
                        "What is inside the Grand Gallery?"
                    ),
                    relatedPoiId = "giza-1"
                )
            }
            q.contains("sphinx") || q.contains("nose") -> {
                ChatMessage(
                    text = "Carved out of natural limestone bedrock under Pharaoh Khafre, the Great Sphinx combines a lion's strength with royal human intellect.\n\nThe missing nose was not shot off by Napoleon! Historical sketches from 1737 prove it was already gone. Historical records by 15th-century Arab historian al-Maqrizi state it was chiselled away in 1378 CE by Muhammad Sa'im al-Dahr.",
                    isUser = false,
                    sources = listOf(
                        ChatSource("The Dream Stele of Thutmose IV", "18th Dynasty", 0.96),
                        ChatSource("Al-Maqrizi's Historical Topography (1442 CE)", "Islamic Cairo Era", 0.93)
                    ),
                    suggestedFollowUps = listOf(
                        "What is written on the Dream Stele?",
                        "Why is the body a lion?",
                        "Are there secret chambers underneath?"
                    ),
                    relatedPoiId = "giza-2"
                )
            }
            else -> {
                ChatMessage(
                    text = "Ancient Egyptian civilization spanned over three millennia of extraordinary achievements in astronomy, architecture, and religious philosophy. From Giza's pyramids to Luxor's towering pylons, each monument reflects deep devotion to cosmic order (Ma'at) and eternity.",
                    isUser = false,
                    sources = listOf(
                        ChatSource("Ministry of Tourism and Antiquities Archive", "Pharaonic Eras", 0.94)
                    ),
                    suggestedFollowUps = listOf(
                        "Who was Egypt's greatest female pharaoh?",
                        "What was the Opet Festival in Luxor?",
                        "How was King Tut's tomb found?"
                    )
                )
            }
        }
    }
}
