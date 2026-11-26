package com.equipo.ecoreporte.reportes.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.ecoreporte.clima.entities.ClimaResponse
import com.equipo.ecoreporte.reportes.entities.Reporte
import com.equipo.ecoreporte.reportes.repositories.ReporteRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReporteViewModel(
    private val repository: ReporteRepository
) : ViewModel() {

    var reportes by mutableStateOf<List<Reporte>>(emptyList())
        private set

    var clima by mutableStateOf<ClimaResponse?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var modoOffline by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cargarTodo() {
        viewModelScope.launch {
            cargando = true
            clima = repository.obtenerClimaActual()
            val resultado = repository.obtenerReportesConOffline()
            reportes = resultado.reportes
            modoOffline = resultado.enModoOffline
            if (modoOffline) {
                mensaje = "Modo offline activo: mostrando datos guardados en Room"
            }
            cargando = false
        }
    }

    fun agregarReporte(
        titulo: String,
        descripcion: String,
        categoria: String,
        ubicacion: String
    ): Boolean {
        if (titulo.isBlank() || descripcion.isBlank()) {
            mensaje = "Por favor escribe al menos un título y una descripción"
            return false
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val nuevo = Reporte(
            titulo = titulo.trim(),
            descripcion = descripcion.trim(),
            categoria = categoria.trim().ifBlank { "General" },
            ubicacion = ubicacion.trim().ifBlank { "Sin ubicación" },
            fecha = fechaActual
        )

        viewModelScope.launch {
            cargando = true
            val (guardado, fueOffline) = repository.crearReporte(nuevo)
            reportes = listOf(guardado) + reportes
            modoOffline = fueOffline
            mensaje = if (fueOffline) {
                "Guardado en caché local (se sincronizará cuando haya conexión)"
            } else {
                "Reporte guardado y sincronizado en la nube"
            }
            cargando = false
        }
        return true
    }

    fun eliminarReporte(id: String?) {
        if (id.isNullOrBlank()) return
        viewModelScope.launch {
            cargando = true
            val eliminadoEnNube = repository.eliminarReporte(id)
            reportes = reportes.filterNot { it.id == id }
            mensaje = if (eliminadoEnNube) {
                "Reporte eliminado"
            } else {
                "Reporte eliminado de la caché local"
            }
            cargando = false
        }
    }

    fun limpiarMensaje() {
        mensaje = null
    }
}