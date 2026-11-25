package com.equipo.ecoreporte.reportes.repositories

import com.equipo.ecoreporte.clima.entities.ClimaResponse
import com.equipo.ecoreporte.clima.services.ClimaApiService
import com.equipo.ecoreporte.reportes.entities.Reporte
import com.equipo.ecoreporte.reportes.entities.ReporteEntity
import com.equipo.ecoreporte.reportes.services.ReporteApiService
import com.equipo.ecoreporte.reportes.services.ReporteDao
import java.util.UUID

class ReporteRepository(
    private val apiService: ReporteApiService,
    private val climaService: ClimaApiService,
    private val reporteDao: ReporteDao
) {
    suspend fun obtenerReportesConOffline(): List<Reporte> {
        return try {
            val remotos = apiService.obtenerReportes()
            val entidades = remotos.mapIndexed { index, r ->
                ReporteEntity(
                    id = r.id ?: "remoto_$index",
                    titulo = r.titulo,
                    descripcion = r.descripcion,
                    categoria = r.categoria,
                    ubicacion = r.ubicacion,
                    fecha = r.fecha
                )
            }
            reporteDao.limpiarCache()
            reporteDao.insertarTodos(entidades)
            remotos
        } catch (_: Exception) {
            reporteDao.obtenerTodos().map { e ->
                Reporte(
                    id = e.id,
                    titulo = e.titulo,
                    descripcion = e.descripcion,
                    categoria = e.categoria,
                    ubicacion = e.ubicacion,
                    fecha = e.fecha
                )
            }
        }
    }

    suspend fun crearReporte(reporte: Reporte): Reporte {
        return try {
            val creado = apiService.crearReporte(reporte)
            val entidad = ReporteEntity(
                id = creado.id ?: UUID.randomUUID().toString(),
                titulo = creado.titulo,
                descripcion = creado.descripcion,
                categoria = creado.categoria,
                ubicacion = creado.ubicacion,
                fecha = creado.fecha
            )
            reporteDao.insertarTodos(listOf(entidad))
            creado
        } catch (_: Exception) {
            val localId = reporte.id ?: UUID.randomUUID().toString()
            val entidad = ReporteEntity(
                id = localId,
                titulo = reporte.titulo,
                descripcion = reporte.descripcion,
                categoria = reporte.categoria,
                ubicacion = reporte.ubicacion,
                fecha = reporte.fecha
            )
            reporteDao.insertarTodos(listOf(entidad))
            reporte.copy(id = localId)
        }
    }

    suspend fun obtenerClima(latitud: Double, longitud: Double): ClimaResponse? {
        return try {
            climaService.obtenerClimaActual(latitud, longitud)
        } catch (_: Exception) {
            null
        }
    }
}