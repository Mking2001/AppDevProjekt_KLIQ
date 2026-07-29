package com.kliq.app.service

import android.graphics.Bitmap

interface QrCodeService {
    fun generateProfileQrPayload(userId: String): String
    suspend fun generateQrCodeBitmap(
        userId: String,
        widthPx: Int = 512,
        heightPx: Int = 512
    ): Result<Bitmap>
}
