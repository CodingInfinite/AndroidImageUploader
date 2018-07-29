package com.spartons.imageuploader

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null
    private lateinit var imageView: ImageView
    private val uiHelper = UiHelper()
    private val reference = this

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 546
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        findViewById<Button>(R.id.selectImageButton).setOnClickListener {
            val intent = Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE)
        }
        findViewById<Button>(R.id.uploadImageButton).setOnClickListener {
            if (bitmap != null) {
                val imageBytes = uiHelper.getImageUrl(bitmap!!)
                uploadImage(imageBytes)
            } else
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(imageBytes: String) {
        val materialDialog = uiHelper.showAlwaysCircularProgress(this, "Uploading Image")
        ServiceApi.Factory.getInstance(this)?.uploadImage(imageBytes)
                ?.enqueue(object : Callback<StatusMessageResponse> {
                    override fun onFailure(call: Call<StatusMessageResponse>?, t: Throwable?) {
                        Toast.makeText(reference, "Image uploaded", Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                    }

                    override fun onResponse(call: Call<StatusMessageResponse>?, response: Response<StatusMessageResponse>?) {
                        materialDialog.dismiss()
                        // Error Occurred during uploading
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && RESULT_OK == resultCode) {
            data?.let {
                try {
                    bitmap = uiHelper.decodeUri(this, it.data)
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    if (bitmap != null) imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}
