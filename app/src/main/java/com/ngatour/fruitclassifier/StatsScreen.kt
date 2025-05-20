package com.ngatour.fruitclassifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatsScreen(viewModel: HistoryViewModel) {
    val stats = viewModel.getStats()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("📊 Statistik Klasifikasi", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Total Klasifikasi: ${stats.total}")
        Text("Confidence Rata-Rata: ${"%.2f".format(stats.averageConfidence)}%")
        Text("Buah Paling Sering Terdeteksi: ${stats.mostFrequentLabel}")
        Text("Klasifikasi Terakhir: ${stats.lastTime}")
    }
}
