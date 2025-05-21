package com.ngatour.fruitclassifier

data class BatchEvaluationResult(
    val total: Int,
    val recognized: Int,
    val unrecognized: Int,
    val avgConfidence: Float,
    val detailedResults: List<ClassificationResult>
)
data class SupabaseHistory(
    val label: String,
    val confidence: Float,
    val description: String,
    val timestamp: String,
    val username: String
)
