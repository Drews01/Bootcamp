package com.example.bootcamp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {

    private const val MAX_DIMENSION = 1024
    private const val COMPRESSION_QUALITY = 80 // 0-100

    fun compressImage(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver

            // 1. Decode bounds to check size
            var inputStream: InputStream? = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // 2. Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_DIMENSION, MAX_DIMENSION)
            options.inJustDecodeBounds = false

            // 3. Decode bitmap with inSampleSize
            inputStream = contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            if (bitmap == null) return null

            // 4. Handle Rotation (Optional but good for Camera)
            // For simplicity in this first pass, we might skip Exif if not critical,
            // but let's try to handle it if we can properly read the stream.
            // Since we are reading from a Uri, we might need a library or just rely on the bitmap.
            // Many modern devices rotate the bitmap automatically in decodeStream or we need androidx.exifinterface.
            // For now, let's stick to resizing to ensure safety.

            // 5. Compress to File
            val cacheDir = context.externalCacheDir ?: context.cacheDir
            val compressedFile = File.createTempFile("compressed_", ".jpg", cacheDir)
            val out = FileOutputStream(compressedFile)

            // Compress to JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)

            out.flush()
            out.close()

            // Recycle bitmap to free memory
            bitmap.recycle()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
