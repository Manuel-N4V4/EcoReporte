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

class ReporteViewModel(
    private val repository: ReporteRepository
) : ViewModel() {

    var reportes by mutableStateOf<List<Reporte>>(emptyList())
        private set

    var clima by mutableStateOf<ClimaResponse?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cargarTodo(latitud: Double = 25.5428, longitud: Double = -103.4068) {
        viewModelScope.launch {
            cargando = true
            mensaje = null
            clima = repository.obtenerClima(latitud, longitud)
            reportes = repository.obtenerReportesConOffline()
            cargando = false
        }
    }

    fun agregarReporte(
        titulo: String,
        descripcion: String,
        categoria: String,
        ubicacion: String
    ) {
        if (titulo.isBlank() || descripcion.isBlank()) {
            mensaje = "Escribe título y descripción"
            return
        }
        viewModelScope.launch {
            cargando = true
            val nuevo = Reporte(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria.trim().ifBlank { "General" },
                ubicacion = ubicacion.trim().ifBlank { "Sin ubicación" }
            )
            repository.crearReporte(nuevo)
            reportes = repository.obtenerReportesConOffline()
            mensaje = "Reporte guardado"
            cargando = false
        }
    }
}