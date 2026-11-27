package com.equipo.ecoreporte.reportes.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reportes_cache")
data class ReporteEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val ubicacion: String,
    val fecha: String,
    val sincronizado: Boolean = true
)