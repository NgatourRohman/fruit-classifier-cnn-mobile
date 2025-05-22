package com.ngatour.fruitclassifier.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "classification_history",
    indices = [Index(value = ["timestamp", "userName"], unique = true)]
)
data class ClassificationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val confidence: Float,
    val description: String,
    val timestamp: String,
    val userName: String
)