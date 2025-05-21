package com.ngatour.fruitclassifier.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.data.model.BatchEvaluationResult
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import com.ngatour.fruitclassifier.util.classifyBitmap
import com.ngatour.fruitclassifier.util.uriToBitmap

@Composable
fun StatsScreen(viewModel: HistoryViewModel) {
    val stats = viewModel.getStats()
    val context = LocalContext.current
    val evaluatedResult = remember { mutableStateOf<BatchEvaluationResult?>(null) }

    val launcherBatchPick = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val results = mutableListOf<ClassificationResult>()
        uris.forEach { uri ->
            val bitmap = uriToBitmap(context, uri)
            val result = classifyBitmap(context, bitmap, "model_fruit_mobile.pt")
            results.add(result)
        }

        val classified = results.filter { it.label != "Tidak dikenali" }
        val averageConfidence = if (classified.isNotEmpty()) {
            classified.map { it.confidence }.average().toFloat()
        } else 0f

        evaluatedResult.value = BatchEvaluationResult(
            total = results.size,
            recognized = classified.size,
            unrecognized = results.size - classified.size,
            avgConfidence = averageConfidence,
            detailedResults = results
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("📊 Statistik Klasifikasi", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Total Klasifikasi: ${stats.total}")
        Text("Confidence Rata-Rata: ${"%.2f".format(stats.averageConfidence)}%")
        Text("Buah Paling Sering Terdeteksi: ${stats.mostFrequentLabel}")
        Text("Klasifikasi Terakhir: ${stats.lastTime}")

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { launcherBatchPick.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Evaluasi Banyak Gambar")
        }

        evaluatedResult.value?.let { result ->
            Spacer(modifier = Modifier.height(12.dp))
            Text("📦 Hasil Evaluasi Batch:")
            Text("Total gambar: ${result.total}")
            Text("Dikenali: ${result.recognized}")
            Text("Tidak dikenali: ${result.unrecognized}")
            Text("Confidence rata-rata: ${"%.2f".format(result.avgConfidence)}%")

            Button(
                onClick = {
                    evaluatedResult.value?.let {
                        val results = it.detailedResults
                        results.forEach { res ->
                            if (res.label != "Tidak dikenali") {
                                viewModel.saveToHistory(res)
                            }
                        }
                        Toast.makeText(context, "Berhasil disimpan ke riwayat", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Simpan ke Riwayat")
            }

        }
    }
}
