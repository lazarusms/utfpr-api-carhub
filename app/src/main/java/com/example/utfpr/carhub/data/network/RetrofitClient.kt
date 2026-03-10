package com.example.utfpr.carhub.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/" // Endereço usado para acessar o localhost no emulador Android

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        //.addInterceptor(GeoLocationInterceptor(DatabaseBuilder.getInstance().userLocationDao()))
        .addInterceptor(loggingInterceptor)
        .build()


    val apiService: CarApiService by lazy {
        retrofit.create(CarApiService::class.java)
    }
}
