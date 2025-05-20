package com.ngatour.fruitclassifier.util

import android.content.Context
import java.io.*
import java.net.URL
import java.net.HttpURLConnection

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
    if (file.exists()) return file.absolutePath
    else throw FileNotFoundException("Model not found: $fileName")
}

fun isModelUpToDate(localFile: File, remoteUrl: String): Boolean {
    return try {
        val connection = URL(remoteUrl).openConnection()
        connection.connect()
        val remoteSize = connection.contentLengthLong
        val localSize = localFile.length()
        localSize == remoteSize
    } catch (e: Exception) {
        false // jika gagal dicek, anggap perlu update
    }
}
