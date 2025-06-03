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

    if (file.exists() && file.length() > 0) return file.absolutePath

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
        false
    }
}

fun classifyBitmap(context: Context, bitmap: Bitmap, modelName: String): ClassificationResult {
    val labels = listOf("Banana", "Mango", "Orange", "Pineapple", "Salak")
    val labelDescriptions = mapOf(
        "Banana" to "Banana is classified based on its curved shape, bright yellow skin when ripe, and relatively smooth surface. These characteristics allow CNN models to recognize edge and contour patterns unique to bananas.",
        "Mango" to "Mangoes have an oval shape with skin colors ranging from green, yellow to reddish. Surface texture and complex color gradients are important features for visual classification using CNN.",
        "Orange" to "Oranges are identified by their round symmetric shape and bumpy surface texture. The vibrant orange color and lighting pattern on the peel are key visual features recognized by CNN.",
        "Pineapple" to "Pineapples have a distinct morphology with scaly textured surfaces and mixed yellow-green colors. CNN uses geometric patterns of the skin and crown as primary classification features.",
        "Salak" to "Salak is known for its dark brown scaly skin and teardrop-like shape. The unique scale texture and strong color contrast are the main visual features CNN relies on to distinguish this fruit."
    )

// Uncomment below to use extended label set (with translations)
//
//    val labels = listOf(
//        "Banana",
//        "Durian",
//        "Guava",
//        "Mango",
//        "Mangosteen",
//        "Orange",
//        "Papaya",
//        "Pineapple",
//        "Rambutan",
//        "Salak"
//    )
//
//    val labelDescriptions = mapOf(
//        "Banana" to "Bananas have a long and slightly curved shape with smooth yellow skin when ripe. CNN models recognize its simple contour and uniform color as key features.",
//
//        "Durian" to "Durian is recognized by its large round shape and sharp greenish spines. The spiky texture and shadow between spikes are strong CNN indicators.",
//
//        "Guava" to "Guavas are round to oval with soft-textured green or yellowish skin. CNN detects soft edges and subtle color gradients.",
//
//        "Mango" to "Mangoes are oval-shaped with skin tones from green to orange-red. CNN learns to classify based on silhouette and complex gradient colors.",
//
//        "Mangosteen" to "Mangosteens are perfectly round with deep purple skin and a small crown. CNN relies on its rich color and top crown as visual markers.",
//
//        "Orange" to "Oranges are round with porous bright orange skin. CNN captures the surface texture and distinctive saturation.",
//
//        "Papaya" to "Papayas are large and oval with green-to-orange skin as it ripens. CNN uses its long shape, proportions, and smooth gradients.",
//
//        "Pineapple" to "Pineapples have hexagonal scale-like skin with a leaf crown. CNN identifies its geometric texture and high yellow-green contrast.",
//
//        "Rambutan" to "Rambutans are small round fruits with soft hairs on their red skin. CNN uses the unique hairy texture and vivid color.",
//
//        "Salak" to "Salak (snake fruit) is teardrop-shaped with dark brown scaly skin. CNN recognizes the high-contrast, scale-like surface."
//    )

    val threshold = 0.75f

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
    val confidenceRaw = probs[maxIdx]
    val confidencePercent = confidenceRaw * 100

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val timestamp = dateFormat.format(Date())

    return if (confidenceRaw >= threshold) {
        val label = labels.getOrElse(maxIdx) { "Unknown" }
        val description = labelDescriptions[label] ?: "No description available."
        ClassificationResult(label, confidencePercent, description, duration, timestamp)
    } else {
        ClassificationResult(
            label = "Not recognized",
            confidence = confidencePercent,
            description = "The image does not match any known fruit class.",
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
