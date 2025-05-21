package com.ngatour.fruitclassifier.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "camera_image.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
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
