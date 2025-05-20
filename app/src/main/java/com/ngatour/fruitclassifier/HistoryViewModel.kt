package com.ngatour.fruitclassifier

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).historyDao()

    private val _history = MutableStateFlow<List<ClassificationHistoryEntity>>(emptyList())
    val history: StateFlow<List<ClassificationHistoryEntity>> = _history

    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _history.value = dao.getAll()
        }
    }

    fun saveToHistory(result: ClassificationResult) {
        val prefs = UserPreferences(getApplication()) // Context dari AndroidViewModel
        val item = ClassificationHistoryEntity(
            label = result.label,
            confidence = result.confidence,
            description = result.description,
            timestamp = result.timestamp,
            userName = prefs.name // ← Ambil nama user aktif
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAll()
            loadHistory()
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteById(id)
            loadHistory()
        }
    }

    fun getStats(): ClassificationStats {
        val history = history.value
        if (history.isEmpty()) return ClassificationStats()

        val total = history.size
        val averageConfidence = history.map { it.confidence }.average().toFloat()
        val mostFrequent = history.groupBy { it.label }
            .maxByOrNull { it.value.size }?.key ?: "Tidak tersedia"
        val lastTimestamp = history.maxByOrNull { it.timestamp }?.timestamp ?: "-"

        return ClassificationStats(total, averageConfidence, mostFrequent, lastTimestamp)
    }


}
