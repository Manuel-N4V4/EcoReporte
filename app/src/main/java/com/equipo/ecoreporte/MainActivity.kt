package com.equipo.ecoreporte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.equipo.ecoreporte.clima.services.ClimaApiService
import com.equipo.ecoreporte.core.network.RetrofitClient
import com.equipo.ecoreporte.reportes.repositories.ReporteRepository
import com.equipo.ecoreporte.reportes.services.AppDatabase
import com.equipo.ecoreporte.reportes.services.ReporteApiService
import com.equipo.ecoreporte.reportes.ui.EcoReporteScreen
import com.equipo.ecoreporte.reportes.viewmodels.ReporteViewModel
import com.equipo.ecoreporte.ui.theme.EcoReporteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val climaService = RetrofitClient.climaRetrofit.create(ClimaApiService::class.java)
        val reporteApiService = RetrofitClient.cloudRetrofit.create(ReporteApiService::class.java)
        val repository = ReporteRepository(
            apiService = reporteApiService,
            climaService = climaService,
            reporteDao = db.reporteDao()
        )
        val viewModel = ReporteViewModel(repository)

        setContent {
            EcoReporteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcoReporteScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}