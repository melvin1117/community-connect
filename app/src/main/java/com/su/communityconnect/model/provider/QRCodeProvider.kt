package com.su.communityconnect.model.provider

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun generateQRCode(content: String): Bitmap = withContext(Dispatchers.Default) {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 256, 256)
    val bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.RGB_565)
    for (x in 0 until 256) {
        for (y in 0 until 256) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return@withContext bitmap
}
