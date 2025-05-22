package com.ngatour.fruitclassifier.ui.stats

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ngatour.fruitclassifier.data.model.BatchEvaluationResult
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.util.classifyBitmap
import com.ngatour.fruitclassifier.util.uriToBitmap

@Composable
fun StatsScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsState()
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
        val avgConf = if (classified.isNotEmpty()) {
            classified.map { it.confidence }.average().toFloat()
        } else 0f

        evaluatedResult.value = BatchEvaluationResult(
            total = results.size,
            recognized = classified.size,
            unrecognized = results.size - classified.size,
            avgConfidence = avgConf,
            detailedResults = results
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("📊 Statistik Klasifikasi", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Label distribution (Pie Chart)
        val labelDistribution = history.groupingBy { it.label }.eachCount()
        if (labelDistribution.isNotEmpty()) {
            Text("Distribusi Label:", style = MaterialTheme.typography.titleMedium)
            PieChart(data = labelDistribution)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confidence rata-rata by label (Bar Chart)
        val avgConfidencePerLabel = history
            .groupBy { it.label }
            .mapValues { (_, list) -> list.map { it.confidence }.average().toFloat() }

        if (avgConfidencePerLabel.isNotEmpty()) {
            Text("Confidence Rata-rata per Label:", style = MaterialTheme.typography.titleMedium)
            BarChart(data = avgConfidencePerLabel)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { launcherBatchPick.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Evaluasi Banyak Gambar")
        }

        evaluatedResult.value?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("📦 Hasil Evaluasi:")
            Text("Total gambar: ${result.total}")
            Text("Dikenali: ${result.recognized}")
        }
    }
}