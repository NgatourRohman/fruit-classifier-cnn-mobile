package com.ngatour.fruitclassifier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(history) { item ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Label: ${item.label}")
                    Text("Confidence: ${"%.2f".format(item.confidence)}%")
                    Text("Waktu: ${item.timestamp}")
                    Text("Deskripsi: ${item.description}")
                }
            }
        }
    }
}
