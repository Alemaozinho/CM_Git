package com.example.conversor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Ligar as variáveis
        val editEuro = findViewById<EditText>(R.id.editEuro)
        val btnConvert = findViewById<Button>(R.id.btnConverter)
        val txtResult = findViewById<TextView>(R.id.txtResultado)

        // 2. Configurar o clique do botão
        btnConvert.setOnClickListener {
            val input = editEuro.text.toString()

            if (input.isNotEmpty()) {
                val euros = input.toDouble()

                // Taxa fixa para o exemplo
                val taxaDolar = 1.16
                val resultado = euros * taxaDolar

                // Atualizar a tela
                txtResult.text = "Resultado: $ ${String.format("%.2f", resultado)}"
            } else {
                editEuro.error = "Digite um valor"
            }
        }
    }
}
