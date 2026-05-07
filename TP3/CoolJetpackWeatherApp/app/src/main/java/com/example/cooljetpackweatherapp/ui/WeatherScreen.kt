package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.R
import com.example.cooljetpackweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState()

    // Variáveis para as caixas de texto
    var latText by remember { mutableStateOf(weatherUIState.latitude.toString()) }
    var lonText by remember { mutableStateOf(weatherUIState.longitude.toString()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // INPUTS DE LATITUDE E LONGITUDE
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = latText,
                    onValueChange = {
                        latText = it
                        it.toFloatOrNull()?.let { v -> weatherViewModel.updateLatitude(v) }
                    },
                    label = { Text("Latitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lonText,
                    onValueChange = {
                        lonText = it
                        it.toFloatOrNull()?.let { v -> weatherViewModel.updateLongitude(v) }
                    },
                    label = { Text("Longitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // DADOS DA METEOROLOGIA (EXERCÍCIO 3)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Temperatura: ${weatherUIState.temperature} °C")
                Text(text = "Vento: ${weatherUIState.windspeed} km/h")
                Text(text = "Pressão: ${weatherUIState.seaLevelPressure} hPa")
                Text(text = "Última atualização: ${weatherUIState.time}")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BOTÃO QUE DISPARA O PEDIDO À API
        Button(
            onClick = { weatherViewModel.fetchWeather() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Atualizar")
        }

        // --- EXERCÍCIO 4 (EXTRA COMENTADO) ---
        // Tentei implementar o mapa aqui com OSMDroid, mas como o emulador
        // não está a carregar as tiles (imagens) por falta de rede, comentei.
        /*
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Mapa (Em desenvolvimento)", style = MaterialTheme.typography.titleMedium)
        Card(modifier = Modifier.fillMaxWidth().height(250.dp).padding(top = 8.dp)) {
            // O componente de mapa AndroidView ficaria aqui
        }
        */
    }
}