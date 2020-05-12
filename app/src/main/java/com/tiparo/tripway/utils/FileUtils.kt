package com.tiparo.tripway.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.lang.Exception

object FileUtils {
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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

    fun decodeSampledBitmapFromUriMedia(
        application: Application,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap =
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options().run {
            var input = application.contentResolver.openInputStream(uri)
                ?: throw Exception("Cant't do openInputStream() (before RESIZING). Uri = $uri")

            inJustDecodeBounds = true
            inPreferredConfig = Bitmap.Config.ARGB_8888
            BitmapFactory.decodeStream(input, null, this)
                ?: throw Exception("The image data could not be decoded (before RESIZING). Uri = $uri")

            input.close()

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            input = application.contentResolver.openInputStream(uri)
                ?: throw Exception("Cant't do openInputStream() (after RESIZING). Uri = $uri")

            val bitmap = BitmapFactory.decodeStream(input, null, this)
                ?: throw Exception("The image data could not be decoded (after RESIZING). Uri = $uri")

            input.close()

            bitmap
        }

    fun getAppSpecificPhotoStorageFile(context: Context, photoName: String): File =
    // Get the picture directory that's inside the app-specific directory on
        // external storage.
        //TODO тут могут возникнуть ошибки
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), photoName)
}