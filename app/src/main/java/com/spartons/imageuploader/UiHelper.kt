package com.spartons.imageuploader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class UiHelper {

    @Throws(FileNotFoundException::class)
    fun decodeUri(context: Context, selectedImage: Uri): Bitmap {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(selectedImage), null, o)
        val REQUIRED_SIZE = 140
        var width_tmp = o.outWidth
        var height_tmp = o.outHeight
        var scale = 1
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break
            }
            width_tmp /= 2
            height_tmp /= 2
            scale *= 2
        }
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(selectedImage), null, o2)
    }

    fun getImageUrl(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun showAlwaysCircularProgress(context: Context, content: String): MaterialDialog {
        return MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 100)
                .cancelable(false)
                .theme(Theme.LIGHT)
                .show()
    }
}