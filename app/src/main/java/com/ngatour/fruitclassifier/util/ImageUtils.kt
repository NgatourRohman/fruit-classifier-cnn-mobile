package com.ngatour.fruitclassifier.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageProxy
import com.ngatour.fruitclassifier.data.remote.SupabaseClient
import com.ngatour.fruitclassifier.data.remote.SupabaseConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


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

fun convertToMutableBitmap(source: Bitmap): Bitmap {
    return source.copy(Bitmap.Config.ARGB_8888, true)
}

suspend fun uploadImageToSupabaseStorage(context: Context, imageUri: Uri): String? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri)
    val fileName = "classified_${System.currentTimeMillis()}.jpg"

    return try {
        val requestBody = inputStream?.readBytes()?.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val response = SupabaseClient.storage.uploadImage("classified-image", fileName, requestBody!!)
        if (response.isSuccessful) {
            "${SupabaseConfig.STORAGE_PUBLIC_URL}/storage/v1/object/public/classified-image/$fileName"
        } else {
            Log.e("UPLOAD", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
            null
        }
    } catch (e: Exception) {
        Log.e("UPLOAD", "Exception: ${e.message}")
        null
    }
}


