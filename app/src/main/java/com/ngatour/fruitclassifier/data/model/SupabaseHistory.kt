package com.ngatour.fruitclassifier.data.model

data class SupabaseHistory(
    val label: String,
    val confidence: Float,
    val description: String,
    val timestamp: String,
    val username: String,
    val imageUrl: String,
    val processTimeMs: Long
)

