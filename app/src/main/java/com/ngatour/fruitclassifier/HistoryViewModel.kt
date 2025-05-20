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
        val item = ClassificationHistoryEntity(
            label = result.label,
            confidence = result.confidence,
            description = result.description,
            timestamp = result.timestamp
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

}
