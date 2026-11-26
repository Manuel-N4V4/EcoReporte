package com.equipo.ecoreporte.reportes.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.equipo.ecoreporte.reportes.entities.ReporteEntity

@Dao
interface ReporteDao {
    @Query("SELECT * FROM reportes_cache")
    suspend fun obtenerTodos(): List<ReporteEntity>

    @Query("SELECT * FROM reportes_cache WHERE sincronizado = 0")
    suspend fun obtenerPendientes(): List<ReporteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(reportes: List<ReporteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(reporte: ReporteEntity)

    @Query("DELETE FROM reportes_cache WHERE id = :id")
    suspend fun eliminarPorId(id: String)

    @Query("DELETE FROM reportes_cache WHERE sincronizado = 1")
    suspend fun limpiarSincronizados()
}