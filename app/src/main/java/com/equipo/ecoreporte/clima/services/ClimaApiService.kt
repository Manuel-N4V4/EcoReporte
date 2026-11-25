package com.equipo.ecoreporte.clima.services

import com.equipo.ecoreporte.clima.entities.ClimaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApiService {
    @GET("v1/forecast")
    suspend fun obtenerClimaActual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): ClimaResponse
}