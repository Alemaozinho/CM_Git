package com.example.systeminfoapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.os.Build

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val infoDisplay = findViewById<TextView>(R.id.textViewSystemInfo)

        // 2. Extrair as informações do objeto android.os.Build
        val systemInfo = """
            Manufacturer: ${android.os.Build.MANUFACTURER}
            Model: ${android.os.Build.MODEL}
            Brand: ${android.os.Build.BRAND}
            Type: ${android.os.Build.TYPE}
            User: ${android.os.Build.USER}
            SDK: ${android.os.Build.VERSION.SDK_INT}
            Version Code: ${android.os.Build.VERSION.RELEASE}
    I       ncremental: ${android.os.Build.VERSION.INCREMENTAL}
        """.trimIndent()

        // 3. Exibir as informações no TextView
        infoDisplay.text = systemInfo

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}