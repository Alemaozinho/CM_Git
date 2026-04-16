package com.example.coolweathergallery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var weatherAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.weatherRecyclerView)
        val btn = findViewById<Button>(R.id.btnUpdate)
        val latIn = findViewById<EditText>(R.id.latValue)
        val lonIn = findViewById<EditText>(R.id.longValue)

        weatherAdapter = WeatherAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = weatherAdapter

        btn.setOnClickListener {
            val lat = latIn.text.toString().toDoubleOrNull() ?: 38.7
            val lon = lonIn.text.toString().toDoubleOrNull() ?: -9.1
            fetchWeatherData(lat, lon)
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        Thread {
            try {
                val url = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&hourly=temperature_2m,weathercode&forecast_days=1")
                val reader = InputStreamReader(url.openStream())
                val data = Gson().fromJson(reader, WeatherData::class.java)

                val items = mutableListOf<WeatherItem>()
                for (i in 0 until 12) {
                    items.add(WeatherItem(
                        time = data.hourly.time[i].substringAfter("T"),
                        temp = "${data.hourly.temperature_2m[i]}°C",
                        imageResId = when(data.hourly.weathercode[i]) {
                            0 -> R.drawable.clear
                            1, 2, 3 -> R.drawable.cloudy
                            else -> R.drawable.rain
                        }
                    ))
                }
                runOnUiThread { weatherAdapter.updateData(items) }
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }
}