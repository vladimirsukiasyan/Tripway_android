package com.tiparo.tripway.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun copyPhotoFromOuterStorageToApp(photoUri: Uri, application: Application): Uri? {
        //TODO обработать возможные ошибки и кейсы с файлами
        //TODO понять какой размер для сжатия выбрать
        try {
            val bitmapPhoto = FileUtils.decodeSampledBitmapFromUriMedia(
                application,
                photoUri,
                480,
                640
            )

            //TODO гененрировать уникальные имена
            val file = FileUtils.getAppSpecificPhotoStorageFile(
                application,
                photoUri.lastPathSegment ?: ""
            )
            //TODO определить оптимальный формат файла
            val outF = FileOutputStream(file)

            val compressSuccess =
                bitmapPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outF)

            if (!compressSuccess) {
                throw Exception("Fail when trying to compress bitmap. Uri = $photoUri")
            }
            return Uri.fromFile(file)

        } catch (exception: Throwable) {
            Timber.e(exception)
            return null
        }
    }

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
            var input = try {
                application.contentResolver.openInputStream(uri)
            } catch (exception: Throwable) {
                throw Exception(
                    "Cant't do openInputStream() (before RESIZING). Uri = $uri",
                    exception
                )
            }

            inJustDecodeBounds = true
            inPreferredConfig = Bitmap.Config.ARGB_8888

            try {
                BitmapFactory.decodeStream(input, null, this)
            } catch (exception: Throwable) {
                throw Exception(
                    "The image data could not be decoded (before RESIZING). Uri = $uri",
                    exception
                )
            }

            input?.close()

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            input = try {
                application.contentResolver.openInputStream(uri)
            } catch (exception: Throwable) {
                throw Exception(
                    "Cant't do openInputStream() (after RESIZING). Uri = $uri",
                    exception
                )
            }

            val bitmap = try {
                BitmapFactory.decodeStream(input, null, this)
            } catch (exception: Throwable) {
                throw Exception(
                    "The image data could not be decoded (after RESIZING). Uri = $uri",
                    exception
                )
            }

            input?.close()

            bitmap!!
        }

    fun getAppSpecificPhotoStorageFile(context: Context, photoName: String): File =
    // Get the picture directory that's inside the app-specific directory on
    // external storage.
        //TODO тут могут возникнуть ошибки
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), photoName)
}