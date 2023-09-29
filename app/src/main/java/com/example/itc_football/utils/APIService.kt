package com.example.itc_football.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIService {
    private const val Base_URL = "base_url"
    fun getService(): APIConsumer{
    val builder : Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Base_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getOkHttp())
    }
}