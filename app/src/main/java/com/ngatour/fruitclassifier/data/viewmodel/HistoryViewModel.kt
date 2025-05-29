package com.ngatour.fruitclassifier.data.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ngatour.fruitclassifier.data.db.AppDatabase
import com.ngatour.fruitclassifier.data.db.ClassificationHistoryEntity
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import com.ngatour.fruitclassifier.data.model.ClassificationStats
import com.ngatour.fruitclassifier.data.model.SupabaseHistory
import com.ngatour.fruitclassifier.data.pref.UserPreferences
import com.ngatour.fruitclassifier.data.remote.SupabaseClient
import com.ngatour.fruitclassifier.data.remote.SupabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.Companion.getDatabase(application).historyDao()
    private val api = SupabaseClient.api
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
            userName = prefs.name // ← Retrieve active user name
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    fun deleteById(entity: ClassificationHistoryEntity) {
        viewModelScope.launch {
            dao.deleteById(entity.id) // Local
            try {
                val timestampQuery = "eq.${entity.timestamp}"
                val usernameQuery = "eq.${entity.userName}"
                val response = api.deleteByTimestampAndUsername(timestampQuery, usernameQuery)
                Log.d("Supabase", "DELETE Cloud status: ${response.code()}")
            } catch (e: Exception) {
                Log.e("Supabase", "Cloud delete failed: ${e.message}")
            }
            loadHistory()
        }
    }

    fun deleteAll(context: Context) {
        val username = UserPreferences(context).name

        viewModelScope.launch {
            // Local delete
            dao.deleteAll()

            // Delete in Supabase by username
            try {
                val usernameQuery = "eq.$username"
                val response = api.deleteAllByUsername(usernameQuery)
                Log.d("Supabase", "DELETE ALL status: ${response.code()}")
            } catch (e: Exception) {
                Log.e("Supabase", "Gagal hapus semua cloud: ${e.message}")
            }

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

    fun exportToCsv(context: Context): File? {
        val history = history.value
        if (history.isEmpty()) return null

        val csvHeader = "Label,Confidence,Timestamp,Deskripsi\n"
        val csvBody = history.joinToString("\n") {
            "${it.label},${it.confidence},${it.timestamp},\"${it.description}\""
        }

        return try {
            val file = File(context.getExternalFilesDir(null), "classification_history.csv")
            file.writeText(csvHeader + csvBody)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun syncFromSupabase(context: Context) {
        val username = UserPreferences(context).name

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val service = SupabaseClient.retrofit.create(SupabaseService::class.java)
                val remoteData = service.getHistory()
                val dao = AppDatabase.getDatabase(context).historyDao()

                remoteData.forEach {
                    try {
                        // only insert if it doesn't exist (because Supabase has constraints, you can ignore the error)
                        dao.insert(
                            ClassificationHistoryEntity(
                                label = it.label,
                                confidence = it.confidence,
                                description = it.description,
                                timestamp = it.timestamp,
                                userName = it.username
                            )
                        )
                    } catch (e: Exception) {
                        // possible duplicates, skip it
                    }
                }
            } catch (e: Exception) {
                Log.e("SYNC", "Gagal sinkronisasi dari cloud: ${e.localizedMessage}")
            }
        }
    }

    fun uploadToSupabaseSingle(result: ClassificationResult, context: Context) {
        val username = UserPreferences(context).name

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val service = SupabaseClient.retrofit.create(SupabaseService::class.java)

                val uploadData = SupabaseHistory(
                    label = result.label,
                    confidence = result.confidence,
                    description = result.description,
                    timestamp = result.timestamp,
                    username = username
                )
                service.uploadHistory(listOf(uploadData)) // use list for bulk POST
            } catch (e: Exception) {
                Log.e("UPLOAD", "Gagal upload: ${e.localizedMessage}")
            }
        }
    }
}