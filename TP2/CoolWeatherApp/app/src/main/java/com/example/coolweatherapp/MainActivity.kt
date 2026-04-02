package com.example.coolweatherapp

import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    var day = true

    override fun onCreate(savedInstanceState: Bundle?) {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (day) setTheme(R.style.Theme_Day) else setTheme(R.style.Theme_Night)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (day) setTheme(R.style.Theme_Day_Land) else setTheme(R.style.Theme_Night_Land)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cast explícito para evitar erros de inferência
        val btnUpdate: Button = findViewById(R.id.btnUpdate)
        val latInput: EditText = findViewById(R.id.latValue)
        val longInput: EditText = findViewById(R.id.longValue)

        btnUpdate.setOnClickListener {
            val lat = latInput.text.toString().toFloatOrNull() ?: 38.76f
            val lon = longInput.text.toString().toFloatOrNull() ?: -9.12f
            fetchWeatherData(lat, lon).start()
        }
    }

    private fun weatherAPI_Call(lat: Float, lon: Float): WeatherData? {
        val reqString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true&hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m"

        return try {
            val url = URL(reqString)
            url.openStream().use { inputStream ->
                Gson().fromJson(InputStreamReader(inputStream, "UTF-8"), WeatherData::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchWeatherData(lat: Float, lon: Float): Thread {
        return Thread {
            val weather = weatherAPI_Call(lat, lon)
            if (weather != null) {
                runOnUiThread {
                    updateUI(weather)
                }
            }
        }
    }

    private fun updateUI(data: WeatherData) {
        val pressureText: TextView = findViewById(R.id.pressureValue)
        val tempText: TextView = findViewById(R.id.tempValue)
        val windText: TextView = findViewById(R.id.windValue)
        val weatherImage: ImageView = findViewById(R.id.weatherImage)

        if (data.hourly.pressure_msl.isNotEmpty()) {
            pressureText.text = "Pressão: ${data.hourly.pressure_msl[0]} hPa"
        }

        tempText.text = "${data.current_weather.temperature} °C"
        windText.text = "Vento: ${data.current_weather.windspeed} km/h"

        val map = getWeatherCodeMap()
        val wCode = map[data.current_weather.weathercode]
        val wImageName = wCode?.image ?: "clear"

        val resID = resources.getIdentifier(wImageName, "drawable", packageName)
        if (resID != 0) {
            weatherImage.setImageResource(resID)
        }

        Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show()
    }
}