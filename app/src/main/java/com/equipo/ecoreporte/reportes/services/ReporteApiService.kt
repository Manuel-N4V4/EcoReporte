package com.equipo.ecoreporte.reportes.services

import com.equipo.ecoreporte.reportes.entities.Reporte
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReporteApiService {
    @GET("reportes")
    suspend fun obtenerReportes(): List<Reporte>

    @POST("reportes")
    suspend fun crearReporte(@Body reporte: Reporte): Reporte

    @DELETE("reportes/{id}")
    suspend fun eliminarReporte(@Path("id") id: String)
}