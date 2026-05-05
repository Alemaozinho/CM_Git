package com.example.app

fun main() {
    val input = "Name: John Address: 123 Street"

    // O IntelliJ deve reconhecer o Extractor agora
    val extractor = DataProcessorExtractor(input)

    println("--- Teste de Extração Regex ---")
    println("Nome extraído: ${extractor.getName()}")
    println("Morada extraída: ${extractor.getAddress()}")
}