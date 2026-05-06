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
    // Observa o estado que vem do ViewModel
    val weatherUIState by weatherViewModel.uiState.collectAsState()

    // Variáveis locais para controlar o texto que escrevemos nas caixas (permite escrever o ponto decimal)
    var latText by remember { mutableStateOf(weatherUIState.latitude.toString()) }
    var lonText by remember { mutableStateOf(weatherUIState.longitude.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // 1. TÍTULO DA APP
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        // 2. CARTÃO DAS COORDENADAS (Com Caixas de Texto Editáveis)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.title_coordinates),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Caixa da Latitude
                OutlinedTextField(
                    value = latText,
                    onValueChange = { novoTexto ->
                        latText = novoTexto // Atualiza o texto no ecrã
                        // Se for um número válido, atualiza no ViewModel
                        novoTexto.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
                    },
                    label = { Text(stringResource(id = R.string.label_latitude)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Caixa da Longitude
                OutlinedTextField(
                    value = lonText,
                    onValueChange = { novoTexto ->
                        lonText = novoTexto
                        novoTexto.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
                    },
                    label = { Text(stringResource(id = R.string.label_longitude)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 3. CARTÃO DA METEOROLOGIA (Resultados)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.title_weather),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${stringResource(id = R.string.label_temperature)} ${weatherUIState.temperature} °C")
                Text(text = "${stringResource(id = R.string.label_wind_speed)} ${weatherUIState.windspeed} km/h")
                Text(text = "${stringResource(id = R.string.label_wind_direction)} ${weatherUIState.winddirection}°")
                Text(text = "${stringResource(id = R.string.label_pressure)} ${weatherUIState.seaLevelPressure} hPa")
                Text(text = "${stringResource(id = R.string.label_time)} ${weatherUIState.time}")
            }
        }

        // 4. BOTÃO DE ATUALIZAR
        Button(
            onClick = {
                println("BOTÃO CLICADO!") // Isto vai ajudar-nos a ver no Logcat
                weatherViewModel.fetchWeather()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.btn_update))
        }
    }
}