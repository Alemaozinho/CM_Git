package com.example.app

import com.example.app.MyClassWrapper
import com.example.app.DataProcessorExtractor

fun main() {
    // --- OUTPUT DO EXERCÍCIO 1 ---
    val myClass = MyClass()
    val wrapped = MyClassWrapper(myClass)
    wrapped.teste()

    // --- OUTPUT DO EXERCÍCIO 2 ---
    println ("Output do EXERCÍCIO 2")
    val input = "Name: John Address: 123 Street"
    val extractor = DataProcessorExtractor(input)
    println("Name: ${extractor.getName()}")
    println("Address: ${extractor.getAddress()}")
}