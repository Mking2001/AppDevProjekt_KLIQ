package com.kliq.app.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class ImageCompressor(private val context: Context) {

    suspend fun compressAndSaveImage(
        uri: Uri,
        maxDimension: Int = 800,
        quality: Int = 80
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream = contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(IllegalArgumentException("Kann Bild stream nicht öffnen"))

            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            var sampleSize = 1
            while ((options.outWidth / sampleSize) > maxDimension || (options.outHeight / sampleSize) > maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }

            val secondStream = contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(IllegalArgumentException("Kann Bild stream nicht erneut öffnen"))

            var bitmap = BitmapFactory.decodeStream(secondStream, null, decodeOptions)
            secondStream.close()

            if (bitmap == null) {
                return@withContext Result.failure(IllegalStateException("Bitmap konnte nicht dekodiert werden"))
            }

            bitmap = rotateBitmapIfRequired(context, uri, bitmap)

            val scaledBitmap = scaleBitmapToMaxDimension(bitmap, maxDimension)
            if (scaledBitmap != bitmap) {
                bitmap.recycle()
            }

            val storageDir = File(context.filesDir, "profile_images").apply {
                if (!exists()) {
                    mkdirs()
                }
            }

            val fileName = "profile_${UUID.randomUUID()}.jpg"
            val outputFile = File(storageDir, fileName)

            FileOutputStream(outputFile).use { outputStream ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            scaledBitmap.recycle()

            Result.success(outputFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun scaleBitmapToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (width > height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun rotateBitmapIfRequired(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }
}
