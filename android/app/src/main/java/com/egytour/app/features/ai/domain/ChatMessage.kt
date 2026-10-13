package com.egytour.app.features.ai.domain

import java.util.UUID

data class ChatSource(
    val title: String,
    val period: String? = null,
    val confidence: Double? = null
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val sources: List<ChatSource> = emptyList(),
    val suggestedFollowUps: List<String> = emptyList(),
    val relatedPoiId: String? = null
)
