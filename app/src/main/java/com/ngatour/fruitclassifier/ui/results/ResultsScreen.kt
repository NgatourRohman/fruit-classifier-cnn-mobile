package com.ngatour.fruitclassifier.ui.results

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ngatour.fruitclassifier.data.model.BatchEvaluationResult
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.theme.Poppins
import com.ngatour.fruitclassifier.util.classifyBitmap
import com.ngatour.fruitclassifier.util.uploadImageToSupabaseStorage
import com.ngatour.fruitclassifier.util.uriToBitmap
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import java.io.File

@Composable
fun ResultsScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val evaluatedResult = remember { mutableStateOf<BatchEvaluationResult?>(null) }
    val lastBatchUris = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val isProcessing = remember { mutableStateOf(false) }

    val launcherBatchPick = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        lastBatchUris.value = uris
        val results = mutableListOf<ClassificationResult>()
        uris.forEach { uri ->
            val bitmap = uriToBitmap(context, uri)
            val result = classifyBitmap(context, bitmap, "model_fruit_mobile.pt")
            results.add(result)
        }

        val classified = results.filter { it.label != "Not recognized" }
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
        isProcessing.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFF3E0))
            .padding(24.dp)
    ) {

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Classification Result",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Summary",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val avg = if (history.isNotEmpty()) history.map { it.confidence }.average() else 0.0

            SummaryCard(
                title = "Total Image Processed",
                value = history.size.toString(),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Average Confidence",
                value = "${"%.0f".format(avg)}%",
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Batch Evaluation",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Evaluate Batch",
                    fontSize = 18.sp, fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Evaluate the accuracy of the classification results for the entire batch.",
                    modifier = Modifier.padding(end = 16.dp),
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color(0xFFFF6F00),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    isProcessing.value = true
                    launcherBatchPick.launch("image/*")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        clip = false
                    )
                    .height(36.dp)
            ) {
                Text("Evaluate", fontFamily = Poppins, fontSize = 14.sp)
            }
        }

        if (isProcessing.value) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Processing images...",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        evaluatedResult.value?.let {
            EvaluatedResultCard(it)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isProcessing.value = true
                        lastBatchUris.value.forEach { uri ->
                            val bitmap = uriToBitmap(context, uri)
                            val result = classifyBitmap(context, bitmap, "model_fruit_mobile.pt")
                            if (result.label != "Not recognized") {
                                val imageUrl = uploadImageToSupabaseStorage(context, uri)
                                if (imageUrl != null) {
                                    viewModel.saveToHistory(result, imageUrl)
                                    viewModel.uploadToSupabaseSingle(result, imageUrl, context)
                                }
                            }
                        }
                        isProcessing.value = false
                        Toast.makeText(context, "Result saved", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00))
            ) {
                Text(
                    text = "Save Result",
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Export Results",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Export CSV",
                    fontSize = 18.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Export the classification results to a CSV file for further analysis",
                    modifier = Modifier.padding(end = 30.dp),
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color(0xFFFF6F00)
                    )
            }

            Spacer(modifier = Modifier.width(12.dp))

            ExportButton(
                viewModel = viewModel,
                context = context
            )
        }


        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Classification Breakdown",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(15.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Fruit Classification",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(16.dp))

                val labelCount = history.groupingBy { it.label }.eachCount()
                FruitBarChart(labelCount)

                Spacer(modifier = Modifier.height(20.dp))
            }
        }


    }
}

@Composable
fun EvaluatedResultCard(result: BatchEvaluationResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("📦 Batch Evaluation Result", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = Poppins)
        Text("Total Images: ${result.total}", fontFamily = Poppins)
        Text("Recognized: ${result.recognized}", fontFamily = Poppins)
        Text("Unrecognized: ${result.unrecognized}", fontFamily = Poppins)
        Text("Average Confidence: ${"%.2f".format(result.avgConfidence)}%", fontFamily = Poppins)
    }
}

@Composable
fun ExportButton(viewModel: HistoryViewModel, context: android.content.Context) {
    Button(
        onClick = {
            val file: File? = viewModel.exportToCsv(context)
            if (file != null) {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share CSV file via"))
            } else {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .height(36.dp)
    ) {
        Text("Export", fontFamily = Poppins)
    }
}

