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
import com.ngatour.fruitclassifier.data.remote.SupabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.Companion.getDatabase(application).historyDao()

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

    fun uploadAllHistoryToSupabase(context: Context) {
        val history = history.value
        val userName = UserPreferences(context).name

        if (history.isEmpty()) {
            Toast.makeText(context, "Tidak ada data untuk di-upload", Toast.LENGTH_SHORT).show()
            return
        }

        val supaList = history.map {
            SupabaseHistory(
                label = it.label,
                confidence = it.confidence,
                description = it.description,
                timestamp = it.timestamp,
                username = userName
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://txxpufiorcjddravosxp.supabase.co/rest/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(SupabaseService::class.java)

                val response = service.uploadHistory(
                    data = supaList,
                    apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR4eHB1ZmlvcmNqZGRyYXZvc3hwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc3NTU1NDQsImV4cCI6MjA2MzMzMTU0NH0.L__y0lxToEonPfboB6rAyfplVNoNTgu92iDjOAKeq3c",
                    auth = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR4eHB1ZmlvcmNqZGRyYXZvc3hwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc3NTU1NDQsImV4cCI6MjA2MzMzMTU0NH0.L__y0lxToEonPfboB6rAyfplVNoNTgu92iDjOAKeq3c"
                )

                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        201 -> {
                            Toast.makeText(
                                context,
                                "Berhasil upload ke Supabase",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        409 -> {
                            Toast.makeText(
                                context,
                                "Data sudah pernah diupload (duplikat)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            val errorMsg = response.errorBody()?.string() ?: "Tidak diketahui"
                            Log.e("Upload Error", "Kode: ${response.code()}, Body: $errorMsg")
                            Toast.makeText(
                                context,
                                "Gagal upload: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Gagal upload: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun syncFromSupabase(context: Context) {
        val username = UserPreferences(context).name

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://txxpufiorcjddravosxp.supabase.co/rest/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(SupabaseService::class.java)

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
}