package com.ngatour.fruitclassifier.ui.history

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(	containerColor = Color(0xFF4CAF50)),
                onClick = {
                    val file = viewModel.exportToCsv(context)
                    if (file != null) {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Bagikan Riwayat CSV"))
                    } else {
                        Toast.makeText(context, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Export CSV")
            }

            Button(
                onClick = {
                    viewModel.deleteAll()
                    Toast.makeText(context, "Semua riwayat dihapus", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Hapus Semua", color = MaterialTheme.colorScheme.onError)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.uploadAllHistoryToSupabase(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Upload ke Cloud")
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("User: ${item.userName}")
                            Text("Label: ${item.label}")
                            Text("Confidence: ${"%.2f".format(item.confidence)}%")
                            Text("Waktu: ${item.timestamp}")
                            Text("Deskripsi: ${item.description}")
                        }

                        IconButton(onClick = { viewModel.deleteById(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }

            }
        }
    }
}

