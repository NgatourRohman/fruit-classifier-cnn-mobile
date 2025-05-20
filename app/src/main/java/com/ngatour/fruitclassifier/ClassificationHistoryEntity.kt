package com.ngatour.fruitclassifier

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classification_history")
data class ClassificationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val confidence: Float,
    val description: String,
    val timestamp: String
)
