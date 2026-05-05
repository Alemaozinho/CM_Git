package com.example.app
import annotations.Greeting

open class MyClass {
    @Greeting("Olá do MyClass")
    open fun teste() {
        println("A funcionar")
    }
}