package com.equipo.ecoreporte.reportes.entities

data class Reporte(
    val id: String? = null,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val ubicacion: String,
    val fecha: String = "2026-11-24"
)