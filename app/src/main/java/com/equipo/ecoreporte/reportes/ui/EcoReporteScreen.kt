package com.equipo.ecoreporte.reportes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.equipo.ecoreporte.reportes.viewmodels.ReporteViewModel

@Composable
fun EcoReporteScreen(
    viewModel: ReporteViewModel,
    modifier: Modifier = Modifier
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarTodo()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "EcoReporte",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Clima actual",
                    style = MaterialTheme.typography.titleMedium
                )
                val current = viewModel.clima?.current_weather
                if (current != null) {
                    Text(text = "Temperatura: ${current.temperature} °C")
                    Text(text = "Viento: ${current.windspeed} km/h")
                } else {
                    Text(text = "Sin datos de clima por ahora")
                }
            }
        }

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título del reporte") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Button(
            onClick = {
                viewModel.agregarReporte(titulo, descripcion, categoria, ubicacion)
                titulo = ""
                descripcion = ""
                categoria = ""
                ubicacion = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar reporte")
        }

        if (viewModel.cargando) {
            CircularProgressIndicator()
        }

        viewModel.mensaje?.let { msg ->
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Reportes registrados (${viewModel.reportes.size})",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.reportes) { reporte ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = reporte.titulo,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = reporte.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${reporte.categoria} • ${reporte.ubicacion} • ${reporte.fecha}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}