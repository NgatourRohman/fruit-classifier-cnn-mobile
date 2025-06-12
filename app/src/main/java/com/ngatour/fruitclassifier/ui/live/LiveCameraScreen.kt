package com.ngatour.fruitclassifier.ui.live

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.Executors
import androidx.core.graphics.scale
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.theme.Poppins
import com.ngatour.fruitclassifier.util.classifyBitmap

@Composable
fun LiveCameraScreen(viewModel: HistoryViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val label = remember { mutableStateOf<String?>(null) }
    val confidence = remember { mutableStateOf<Float?>(null) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
            val bitmap = imageProxy.toBitmap()
            if (bitmap != null) {
                val resized = bitmap.scale(224, 224)
                val result = classifyBitmap(context, resized, "model_fruit_mobile.pt")

                // Only display classification results, no saving or uploading
                label.value = result.label
                confidence.value = result.confidence
            }
            imageProxy.close()
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalyzer
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Predicted Fruit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (label.value != null && confidence.value != null)
                        "${label.value} (Confidence: ${"%.2f".format(confidence.value!!)}%)"
                    else
                        "Waiting for classification...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
            }
        }
    }
}

