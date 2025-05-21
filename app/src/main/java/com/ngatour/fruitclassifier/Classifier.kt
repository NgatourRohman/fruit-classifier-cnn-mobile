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
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.camera.core.ImageProxy
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.isGranted
import com.ngatour.fruitclassifier.util.downloadModelFromUrl
import com.ngatour.fruitclassifier.util.isModelUpToDate
import com.ngatour.fruitclassifier.util.modelFilePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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

fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

fun ImageProxy.toBitmap(): Bitmap? {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
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

    val threshold = 0.75f // Minimum confidence that is considered valid

    val module = Module.load(modelFilePath(context, "model_fruit_mobile.pt"))
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
    val confidenceRaw = probs[maxIdx] // still within 0.0 - 1.0
    val confidencePercent = confidenceRaw * 100

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
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

fun softmax(logits: FloatArray): FloatArray {
    val expScores = logits.map { exp(it.toDouble()) }
    val sumExp = expScores.sum()
    return expScores.map { (it / sumExp).toFloat() }.toFloatArray()
}

fun generatePDF(context: Context, result: ClassificationResult): File {
    val pdfDoc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 400, 1).create()
    val page = pdfDoc.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    var y = 20
    val spacing = 20

    canvas.drawText("Fruit Classification Result", 10f, y.toFloat(), paint)
    y += spacing
    canvas.drawText("Label: ${result.label}", 10f, y.toFloat(), paint)
    y += spacing
    canvas.drawText("Confidence: ${"%.2f".format(result.confidence)}%", 10f, y.toFloat(), paint)
    y += spacing
    canvas.drawText("Time: ${result.timestamp}", 10f, y.toFloat(), paint)
    y += spacing
    canvas.drawText("Description:", 10f, y.toFloat(), paint)
    y += spacing
    canvas.drawText(result.description, 10f, y.toFloat(), paint)

    pdfDoc.finishPage(page)

    val file = File(context.getExternalFilesDir(null), "result_${System.currentTimeMillis()}.pdf")
    pdfDoc.writeTo(FileOutputStream(file))
    pdfDoc.close()

    return file
}

fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Bagikan hasil klasifikasi"))
}
