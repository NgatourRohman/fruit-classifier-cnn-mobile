package com.ngatour.fruitclassifier

data class BatchEvaluationResult(
    val total: Int,
    val recognized: Int,
    val unrecognized: Int,
    val avgConfidence: Float,
    val detailedResults: List<ClassificationResult>
)