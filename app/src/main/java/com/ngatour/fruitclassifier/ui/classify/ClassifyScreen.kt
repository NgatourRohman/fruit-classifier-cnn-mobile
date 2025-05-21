package com.ngatour.fruitclassifier.ui.classify

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.unit.dp
import android.Manifest
import android.widget.Toast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.isGranted
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import com.ngatour.fruitclassifier.util.classifyBitmap
import com.ngatour.fruitclassifier.util.downloadModelFromUrl
import com.ngatour.fruitclassifier.util.generatePDF
import com.ngatour.fruitclassifier.util.isModelUpToDate
import com.ngatour.fruitclassifier.util.saveBitmapToCache
import com.ngatour.fruitclassifier.util.shareFile
import com.ngatour.fruitclassifier.util.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalPermissionsApi::class,ExperimentalMaterial3Api::class)
@Composable
fun FruitClassifierScreen(viewModel: HistoryViewModel) {

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: HistoryViewModel = viewModel()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var result by remember { mutableStateOf<ClassificationResult?>(null) }

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = saveBitmapToCache(context, it)
            imageUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Klasifikasi Buah Tropis") })
        },
        bottomBar = {
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val modelUrl = "https://txxpufiorcjddravosxp.supabase.co/storage/v1/object/public/models//model_fruit_mobile.pt"
                        val localFile = File(context.filesDir, "model_fruit_mobile.pt")
                        val upToDate = if (localFile.exists()) isModelUpToDate(localFile, modelUrl) else false

                        withContext(Dispatchers.Main) {
                            if (upToDate) {
                                Toast.makeText(context, "Model sudah diperbarui", Toast.LENGTH_SHORT).show()
                            } else {
                                val file = downloadModelFromUrl(context, modelUrl, "model_fruit_mobile.pt")
                                if (file != null) {
                                    Toast.makeText(context, "Model berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Gagal memperbarui model", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Perbarui Model")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!cameraPermissionState.status.isGranted) {
                Text(
                    "Aplikasi membutuhkan izin kamera untuk mengambil gambar.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(8.dp)
                )
            } ?: Text("Silakan pilih atau ambil gambar", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = { launcherGallery.launch("image/*") }) {
                    Text("Galeri")
                }
                Button(onClick = { launcherCamera.launch(null) }) {
                    Text("Kamera")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    imageUri?.let {
                        val bitmap = uriToBitmap(context, it)
                        result = classifyBitmap(context, bitmap, "model_fruit_mobile.pt")
                        viewModel.saveToHistory(result!!)
                    }
                },
                enabled = imageUri != null
            ) {
                Text("Klasifikasi Gambar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            result?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📌 Hasil Klasifikasi", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Label: ${it.label}")
                        Text("• Confidence: ${"%.2f".format(it.confidence)}%")
                        Text("• Waktu Proses: ${it.processTimeMs} ms")
                        Text("• Tanggal & Jam: ${it.timestamp}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Deskripsi: ${it.description}")

                        Button(
                            onClick = {
                                val pdfFile = generatePDF(context, it)
                                shareFile(context, pdfFile)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Ekspor & Bagikan")
                        }
                    }
                }
            }
        }
    }
}
