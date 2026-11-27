package com.equipo.ecoreporte.reportes.repositories

import com.equipo.ecoreporte.clima.entities.ClimaResponse
import com.equipo.ecoreporte.clima.services.ClimaApiService
import com.equipo.ecoreporte.reportes.entities.Reporte
import com.equipo.ecoreporte.reportes.entities.ReporteEntity
import com.equipo.ecoreporte.reportes.services.ReporteApiService
import com.equipo.ecoreporte.reportes.services.ReporteDao
import java.util.UUID

data class ResultadoReportes(
    val reportes: List<Reporte>,
    val enModoOffline: Boolean
)

class ReporteRepository(
    private val apiService: ReporteApiService,
    private val climaService: ClimaApiService,
    private val reporteDao: ReporteDao
) {
    suspend fun obtenerClimaActual(latitud: Double = 25.5428, longitud: Double = -103.4068): ClimaResponse? {
        return try {
            climaService.obtenerClimaActual(latitud, longitud)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerReportesConOffline(): ResultadoReportes {
        return try {
            // 1. Intentar sincronizar reportes creados sin conexión
            val pendientes = reporteDao.obtenerPendientes()
            for (pendiente in pendientes) {
                try {
                    val creadoEnNube = apiService.crearReporte(pendiente.toDomain())
                    reporteDao.eliminarPorId(pendiente.id)
                    reporteDao.insertar(creadoEnNube.toEntity(sincronizado = true))
                } catch (_: Exception) {
                    // Si alguno falla, permanece pendiente en Room
                }
            }

            // 2. Obtener lista actualizada de la nube
            val remotos = apiService.obtenerReportes()
            reporteDao.limpiarSincronizados()
            reporteDao.insertarTodos(remotos.map { it.toEntity(sincronizado = true) })

            val todosActualizados = reporteDao.obtenerTodos().map { it.toDomain() }
            ResultadoReportes(reportes = todosActualizados, enModoOffline = false)
        } catch (e: Exception) {
            val locales = reporteDao.obtenerTodos().map { it.toDomain() }
            ResultadoReportes(reportes = locales, enModoOffline = true)
        }
    }

    suspend fun crearReporte(reporte: Reporte): Pair<Reporte, Boolean> {
        return try {
            val creado = apiService.crearReporte(reporte)
            reporteDao.insertar(creado.toEntity(sincronizado = true))
            creado to false // false = guardado en nube
        } catch (e: Exception) {
            val idLocal = reporte.id?.takeIf { it.isNotBlank() } ?: "local_${UUID.randomUUID()}"
            val reporteOffline = reporte.copy(id = idLocal)
            reporteDao.insertar(reporteOffline.toEntity(sincronizado = false))
            reporteOffline to true // true = guardado solo en Room (offline)
        }
    }

    suspend fun eliminarReporte(id: String): Boolean {
        return try {
            if (!id.startsWith("local_")) {
                apiService.eliminarReporte(id)
            }
            reporteDao.eliminarPorId(id)
            true
        } catch (e: Exception) {
            reporteDao.eliminarPorId(id)
            false // Eliminado localmente aunque no hubo respuesta de nube
        }
    }
}

private fun Reporte.toEntity(sincronizado: Boolean) = ReporteEntity(
    id = id ?: "local_${UUID.randomUUID()}",
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    ubicacion = ubicacion,
    fecha = fecha,
    sincronizado = sincronizado
)

private fun ReporteEntity.toDomain() = Reporte(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    ubicacion = ubicacion,
    fecha = fecha
)