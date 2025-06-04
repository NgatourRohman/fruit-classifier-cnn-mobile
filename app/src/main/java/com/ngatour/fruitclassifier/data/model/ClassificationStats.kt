package com.ngatour.fruitclassifier.data.model

data class ClassificationStats(
    val total: Int = 0,
    val averageConfidence: Float = 0f,
    val mostFrequentLabel: String = "-",
    val lastTime: String = "-"
)