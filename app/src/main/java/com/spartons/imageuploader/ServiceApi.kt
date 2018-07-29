package com.spartons.imageuploader

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File
import java.util.concurrent.TimeUnit

interface ServiceApi {

    companion object {
        private const val BASE_URL = ""  // Add Base url of your hosting
    }

    @FormUrlEncoded
    @POST("")   // Add end_point url
    fun uploadImage(@Field("image_bytes") imageBytes: String): Call<StatusMessageResponse>

    class Factory {

        companion object {
            private var service: ServiceApi? = null
            fun getInstance(context: Context): ServiceApi? {
                if (service == null) {
                    val builder = OkHttpClient().newBuilder()
                    builder.readTimeout(15, TimeUnit.SECONDS)
                    builder.connectTimeout(15, TimeUnit.SECONDS)
                    builder.writeTimeout(15, TimeUnit.SECONDS)
                    if (BuildConfig.DEBUG) {
                        val interceptor = HttpLoggingInterceptor()
                        interceptor.level = HttpLoggingInterceptor.Level.BODY
                        builder.addInterceptor(interceptor)
                    }
                    val file = File(context.cacheDir, "cache_dir")
                    if (!file.exists())
                        file.mkdirs()
                    val cacheSize: Long = 10 * 1024 * 1024 // 10 MiB
                    val cache = okhttp3.Cache(file, cacheSize)
                    builder.cache(cache)
                    val retrofit: Retrofit
                    retrofit = Retrofit.Builder()
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(BASE_URL)
                            .build()
                    service = retrofit.create(ServiceApi::class.java)
                    return service
                } else {
                    return service
                }
            }
        }
    }
}