package com.example.a26apigooglemap.Request

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ApiClient @Inject constructor() {

    val intercepter = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder().apply {
        this.addInterceptor(intercepter)
    }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}