package com.ngatour.fruitclassifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import android.Manifest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.isGranted
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.exp

@OptIn(ExperimentalPermissionsApi::class,ExperimentalMaterial3Api::class)
@Composable
fun FruitClassifierApp(viewModel: HistoryViewModel) {

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val context = LocalContext.current
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

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

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
                    }
                }
            }
        }
    }
}
fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "camera_image.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun classifyBitmap(context: Context, bitmap: Bitmap, modelName: String): ClassificationResult {
    val labels = listOf("Banana", "Mango", "Orange", "Pineapple", "Salak")
    val labelDescriptions = mapOf(
        "Banana" to "Pisang adalah buah tropis yang kaya potasium dan vitamin B6.",
        "Mango" to "Mangga memiliki rasa manis dan tekstur lembut, kaya akan vitamin C.",
        "Orange" to "Jeruk merupakan sumber vitamin C, biasa dikonsumsi sebagai jus.",
        "Pineapple" to "Nanas memiliki rasa asam-manis dan tinggi enzim bromelain.",
        "Salak" to "Salak atau snake fruit memiliki rasa manis dan sedikit sepat."
    )

    val threshold = 0.75f // Confidence minimum yang dianggap valid

    val module = Module.load(assetFilePath(context, modelName))
    val safeBitmap = convertToMutableBitmap(bitmap)
    val resized = Bitmap.createScaledBitmap(safeBitmap, 224, 224, true)

    val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
        resized,
        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
        TensorImageUtils.TORCHVISION_NORM_STD_RGB
    )

    val startTime = System.currentTimeMillis()
    val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime

    val rawScores = outputTensor.dataAsFloatArray
    val probs = softmax(rawScores)
    val maxIdx = probs.indices.maxByOrNull { probs[it] } ?: -1
    val confidenceRaw = probs[maxIdx] // masih dalam 0.0 - 1.0
    val confidencePercent = confidenceRaw * 100

    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    val timestamp = dateFormat.format(Date())

    return if (confidenceRaw >= threshold) {
        val label = labels.getOrElse(maxIdx) { "Tidak Dikenal" }
        val description = labelDescriptions[label] ?: "Tidak ada deskripsi."
        ClassificationResult(label, confidencePercent, description, duration, timestamp)
    } else {
        ClassificationResult(
            label = "Tidak dikenali",
            confidence = confidencePercent,
            description = "Gambar tidak sesuai dengan kelas buah yang telah dikenali.",
            processTimeMs = duration,
            timestamp = timestamp
        )
    }
}

fun convertToMutableBitmap(source: Bitmap): Bitmap {
    return source.copy(Bitmap.Config.ARGB_8888, true)
}

fun assetFilePath(context: Context, assetName: String): String {
    val file = File(context.filesDir, assetName)
    if (file.exists() && file.length() > 0) return file.absolutePath

    context.assets.open(assetName).use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1)
                outputStream.write(buffer, 0, read)
            outputStream.flush()
        }
    }
    return file.absolutePath
}

fun softmax(logits: FloatArray): FloatArray {
    val expScores = logits.map { exp(it.toDouble()) }
    val sumExp = expScores.sum()
    return expScores.map { (it / sumExp).toFloat() }.toFloatArray()
}

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val description: String,
    val processTimeMs: Long,
    val timestamp: String
)