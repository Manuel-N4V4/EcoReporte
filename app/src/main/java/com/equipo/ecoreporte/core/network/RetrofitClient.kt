package com.equipo.ecoreporte.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val CLOUD_BASE_URL = "https://tu-api-cloud.ejemplo.com/"
    private const val CLIMA_BASE_URL = "https://api.open-meteo.com/"

    val cloudRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CLOUD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val climaRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CLIMA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}