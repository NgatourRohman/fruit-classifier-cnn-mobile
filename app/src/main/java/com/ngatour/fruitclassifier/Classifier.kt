package com.ngatour.fruitclassifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun FruitClassifierApp() {
    var prediction by remember { mutableStateOf("Belum ada hasil") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.fruit_sample),
            contentDescription = "Buah Sample",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = prediction)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            prediction = "Prediksi: " + classifyFruit(context, "model_fruit_mobile.pt", R.drawable.fruit_sample)
        }) {
            Text("Klasifikasi Gambar")
        }
    }
}

fun classifyFruit(context: Context, modelName: String, imageRes: Int): String {
    val labels = listOf("Banana", "Mango", "Orange", "Pineapple", "Salak")

    val module = Module.load(assetFilePath(context, modelName))
    val bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
    val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

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
