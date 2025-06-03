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
        "Banana" to "Pisang diklasifikasikan berdasarkan bentuknya yang melengkung, kulit berwarna kuning cerah saat matang, dan permukaan yang relatif halus. Karakteristik ini memungkinkan model CNN mengenali pola tepi dan kontur khas buah pisang.",

        "Mango" to "Mangga memiliki bentuk oval dengan warna kulit yang bervariasi dari hijau, kuning hingga kemerahan. Tekstur permukaan serta gradasi warna yang kompleks menjadi fitur penting dalam proses klasifikasi visual menggunakan CNN.",

        "Orange" to "Jeruk diklasifikasikan melalui bentuknya yang bulat simetris dan tekstur permukaan berpori (berbintik halus). Warna oranye menyala dan pola pencahayaan pada kulit merupakan fitur visual utama yang diidentifikasi oleh CNN.",

        "Pineapple" to "Nanas memiliki morfologi khas dengan permukaan bertekstur sisik dan warna campuran antara kuning dan hijau. CNN memanfaatkan pola geometris dari permukaan dan tajuk atas (mahkota) sebagai fitur utama klasifikasi.",

        "Salak" to "Salak dikenal dari kulit bersisik berwarna coklat gelap dan bentuk menyerupai tetesan air. Pola tekstur unik pada kulit dan kontras warna tinggi menjadi aspek visual utama yang dikenali oleh CNN dalam membedakan buah ini."
    )

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
//        "Banana" to "Pisang memiliki bentuk memanjang dan sedikit melengkung, dengan permukaan kulit yang halus dan berwarna kuning cerah saat matang. Model CNN mengenali pola kontur sederhana dan warna seragam sebagai ciri khasnya.",
//
//        "Durian" to "Durian dikenal dari bentuk bulat besar dan kulit berduri tajam berwarna hijau atau cokelat kehijauan. Tekstur berduri yang unik dan bayangan antar duri menjadi fitur visual penting bagi CNN.",
//
//        "Guava" to "Jambu biji memiliki bentuk bulat hingga oval, permukaan kulit bertekstur halus dengan warna hijau atau kekuningan. CNN mendeteksi tepi lembut dan gradasi warna lembut sebagai fitur pembeda.",
//
//        "Mango" to "Mangga berbentuk oval atau lonjong dengan warna kulit yang bervariasi dari hijau, kuning, hingga merah jingga. CNN mengandalkan kombinasi gradasi warna dan siluet buah untuk klasifikasi.",
//
//        "Mangosteen" to "Manggis memiliki kulit luar berwarna ungu tua dengan bentuk bulat sempurna. Fitur visual seperti warna pekat dan mahkota kecil di atas buah menjadi penanda utama untuk model CNN.",
//
//        "Orange" to "Jeruk bulat dengan permukaan kulit berpori dan warna oranye terang. CNN mengenali pola tekstur kulit dan saturasi warna yang khas untuk mengidentifikasi buah ini.",
//
//        "Papaya" to "Pepaya berbentuk lonjong besar dengan kulit hijau saat mentah dan kuning-oranye saat matang. Model CNN memanfaatkan ukuran proporsional, bentuk memanjang, dan warna gradien sebagai fitur klasifikasi.",
//
//        "Pineapple" to "Nanas memiliki kulit bersisik berpola heksagonal dan tajuk daun di bagian atas. CNN mengenali pola geometri sisik dan warna kontras kuning-hijau sebagai ciri utama.",
//
//        "Rambutan" to "Rambutan berbentuk bulat kecil dengan rambut-rambut lembut di kulitnya. CNN menggunakan fitur visual unik seperti tekstur rambut dan warna merah terang sebagai indikator.",
//
//        "Salak" to "Salak memiliki bentuk lonjong seperti tetesan air, dengan kulit bersisik berwarna cokelat gelap. Pola sisik dan permukaan reflektif menjadi ciri khas yang diidentifikasi CNN."
//    )

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

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
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