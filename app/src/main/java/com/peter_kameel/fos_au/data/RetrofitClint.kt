package com.peter_kameel.fos_au.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClint {

    private const val URL = "https://fosau.herokuapp.com"

    // private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder()
    private val gson = GsonBuilder().setLenient().create()

    private val builder = Retrofit.Builder()
        .baseUrl(URL)
        .client(okHttpClient.build())
        .addConverterFactory(GsonConverterFactory.create(gson))

    private val retrofit = builder.build()
    fun <T> retrofitRequest(serves: Class<T>): T {
        return retrofit.create(serves)
    }

}