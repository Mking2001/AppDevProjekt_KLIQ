package com.kliq.app.service

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrCodeServiceImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QrCodeService {

    override fun generateProfileQrPayload(userId: String): String {
        val timestamp = System.currentTimeMillis()
        val staticPart = "kliq_profile_v1"
        return "kliq://user/verify/$userId?tag=$staticPart&ts=$timestamp"
    }

    override suspend fun generateQrCodeBitmap(
        userId: String,
        widthPx: Int,
        heightPx: Int
    ): Result<Bitmap> = withContext(ioDispatcher) {
        try {
            val payload = generateProfileQrPayload(userId)
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(payload, BarcodeFormat.QR_CODE, widthPx, heightPx)

            val pixels = IntArray(widthPx * heightPx)
            for (y in 0 until heightPx) {
                val offset = y * widthPx
                for (x in 0 until widthPx) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, widthPx, 0, 0, widthPx, heightPx)
            Result.success(bitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
