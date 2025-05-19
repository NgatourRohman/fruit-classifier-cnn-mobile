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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun FruitClassifierApp() {
    val context = LocalContext.current
    var prediction by remember { mutableStateOf("Belum ada hasil") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Simpan sementara ke cache dan ambil URI
            val uri = saveBitmapToCache(context, bitmap)
            imageUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(model = it),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )
        }

        Text(text = prediction)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = { launcherGallery.launch("image/*") }) {
                Text("Pilih dari Galeri")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { launcherCamera.launch(null) }) {
                Text("Kamera")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            imageUri?.let {
                val bitmap = uriToBitmap(context, it)
                prediction = "Prediksi: " + classifyBitmap(context, bitmap, "model_fruit_mobile.pt")
            }
        }) {
            Text("Klasifikasi Gambar")
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

fun classifyBitmap(context: Context, bitmap: Bitmap, modelName: String): String {
    val labels = listOf("Banana", "Mango", "Orange", "Pineapple", "Salak")

    val module = Module.load(assetFilePath(context, modelName))

    val safeBitmap = convertToMutableBitmap(bitmap)
    val resized = Bitmap.createScaledBitmap(safeBitmap, 224, 224, true)

    val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
        resized,
        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
        TensorImageUtils.TORCHVISION_NORM_STD_RGB
    )

    val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
    val scores = outputTensor.dataAsFloatArray
    val maxIdx = scores.indices.maxByOrNull { scores[it] } ?: -1

    return labels.getOrElse(maxIdx) { "Tidak Dikenal" }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FruitClassifierPreview() {
    FruitClassifierApp()
}