class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Ligar as variáveis (Isso precisa estar dentro do onCreate)
        val editEuro = findViewById<EditText>(R.id.editEuro)
        val btnConvert = findViewById<Button>(R.id.btnConvert)
        val txtResult = findViewById<TextView>(R.id.txtResultado)

        // 2. Configurar o clique do botão (Também dentro do onCreate)
        btnConvert.setOnClickListener {
            val input = editEuro.text.toString()

            if (input.isNotEmpty()) {
                val euros = input.toDouble()

                // Taxa fixa para o exemplo (ou você pode usar a API que discutimos)
                val taxaDolar = 1.10
                val resultado = euros * taxaDolar

                // Atualizar a tela
                txtResult.text = "Resultado: $ ${String.format("%.2f", resultado)}"
            } else {
                editEuro.error = "Digite um valor"
            }
        }
    }
}