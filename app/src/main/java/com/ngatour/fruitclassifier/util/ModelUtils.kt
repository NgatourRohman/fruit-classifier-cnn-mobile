package com.ngatour.fruitclassifier.util

import android.content.Context
import android.graphics.Bitmap
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.*
import java.net.URL
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.exp

fun downloadModelFromUrl(context: Context, url: String, fileName: String): File? {
    return try {
        val urlObj = URL(url)
        val connection: HttpURLConnection = urlObj.openConnection() as HttpURLConnection
        connection.connect()

        val input = BufferedInputStream(urlObj.openStream())
        val file = File(context.filesDir, fileName)
        val output = FileOutputStream(file)

        val data = ByteArray(1024)
        var count: Int
        while (input.read(data).also { count = it } != -1) {
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun modelFilePath(context: Context, fileName: String): String {
    val file = File(context.filesDir, fileName)

    // If it's already in filesDir, use it
    if (file.exists() && file.length() > 0) return file.absolutePath

    // Fallback: try copying from assets if it's not there yet
    return try {
        context.assets.open(fileName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        throw FileNotFoundException("Model not found in filesDir or assets: $fileName")
    }
}


fun isModelUpToDate(localFile: File, remoteUrl: String): Boolean {
    return try {
        val connection = URL(remoteUrl).openConnection()
        connection.connect()
        val remoteSize = connection.contentLengthLong
        val localSize = localFile.length()
        localSize == remoteSize
    } catch (e: Exception) {
        false // if it fails to check, consider updating
    }
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

    val module = Module.load(modelFilePath(context, modelName))
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

fun softmax(logits: FloatArray): FloatArray {
    val expScores = logits.map { exp(it.toDouble()) }
    val sumExp = expScores.sum()
    return expScores.map { (it / sumExp).toFloat() }.toFloatArray()
}