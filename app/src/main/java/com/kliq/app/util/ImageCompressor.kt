package com.kliq.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

data class CompressedImageResult(
    val mediaUrl: String,
    val thumbnailUrl: String?,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
    val sizeBytes: Long
)

@Singleton
class ImageCompressor @Inject constructor() {

    suspend fun compressAndSaveImage(
        context: Context,
        imageUri: Uri,
        maxDimension: Int = 1280,
        quality: Int = 80
    ): CompressedImageResult = withContext(Dispatchers.IO) {
        val cacheDir = File(context.cacheDir, "chat_images").apply { if (!exists()) mkdirs() }
        val timestamp = System.currentTimeMillis()
        val outputFile = File(cacheDir, "img_$timestamp.jpg")
        val thumbFile = File(cacheDir, "thumb_$timestamp.jpg")

        var inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        var sampleSize = 1
        while (originalWidth / sampleSize > maxDimension || originalHeight / sampleSize > maxDimension) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        inputStream = context.contentResolver.openInputStream(imageUri)
        var bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
        inputStream?.close()

        if (bitmap == null) {
            throw IllegalArgumentException("Bitmap konnte von URI nicht dekodiert werden: $imageUri")
        }

        val rotationAngle = getRotationAngle(context, imageUri)
        if (rotationAngle != 0) {
            bitmap = rotateBitmap(bitmap, rotationAngle)
        }

        val scaledBitmap = scaleBitmapToMax(bitmap, maxDimension)
        val finalWidth = scaledBitmap.width
        val finalHeight = scaledBitmap.height
        val aspectRatio = if (finalHeight > 0) finalWidth.toFloat() / finalHeight.toFloat() else 1.0f

        val fos = FileOutputStream(outputFile)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)
        fos.flush()
        fos.close()

        val thumbBitmap = scaleBitmapToMax(scaledBitmap, 300)
        val thumbFos = FileOutputStream(thumbFile)
        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 60, thumbFos)
        thumbFos.flush()
        thumbFos.close()

        CompressedImageResult(
            mediaUrl = outputFile.absolutePath,
            thumbnailUrl = thumbFile.absolutePath,
            width = finalWidth,
            height = finalHeight,
            aspectRatio = aspectRatio,
            sizeBytes = outputFile.length()
        )
    }

    private fun scaleBitmapToMax(bitmap: Bitmap, maxDim: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetW: Int
        val targetH: Int
        if (width > height) {
            targetW = maxDim
            targetH = (maxDim / ratio).toInt()
        } else {
            targetH = maxDim
            targetW = (maxDim * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
    }

    private fun getRotationAngle(context: Context, uri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return 0
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            inputStream.close()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(angle.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
