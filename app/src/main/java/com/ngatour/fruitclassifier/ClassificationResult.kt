package com.ngatour.fruitclassifier

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val description: String,
    val processTimeMs: Long,
    val timestamp: String
)
