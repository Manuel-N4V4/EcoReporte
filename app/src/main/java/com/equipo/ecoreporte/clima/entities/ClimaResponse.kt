package com.equipo.ecoreporte.clima.entities

data class ClimaResponse(
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double
)